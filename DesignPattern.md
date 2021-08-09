# 使用设计模式讲解

## 一、策略模式

### 前言

> **使用背景**：订阅消息
> **使用原因**：订阅消息存在着多种可能性，目前具有回复消息订阅、评论订阅，暂无其他类型，但是为来更多的扩展性，使用策略模式，将每个类型的具体订阅算法封装，然后在具体使用时候通过订阅上下文获取，如果需要扩展其他的订阅消息类型可以直接进行添加策略类以及对应的枚举即可，之所以定义枚举是因为需要保存每个策略类型

### 代码实现

> 订阅类接口，接口非常简单，只是定义了一个处理订阅消息的方法，具体内容交给对应的
> 订阅消息策略类实现

~~~java

@SuppressWarnings("all")
public interface SubMessageStrategy {
    /**
     * 处理订阅消息，将其
     * @param param 参数
     * @param reserveParam 预留参数
     */
    void processSubMessage(SubMessageParam param, String... reserveParam);
}
~~~

> 枚举类：记录每个类型的信息

~~~java
public enum SubMessageTemplateEnum {
    /**
     * 订阅消息评论模版ID
     */
    SUB_MESSAGE_COMMENT_TEMPLATE_ID("mghtoN9x1YBMmyWg9RtBlt8-XxHxMvEo8eAtHIazD34", "com.yundingxi.tell.util.strategy.SubMessageStrategyContext$CommentSubMessageStrategy"),
    /**
     * 订阅消息回信ID
     */
    SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID("vuxCjKVvzbUWW1iHbMkSCmsBrpXWkXFPJ81S8nVWJdw", "com.yundingxi.tell.util.strategy.SubMessageStrategyContext$ReplySubMessageStrategy");

    //模版ID
    private final String templateId;
    //对应的class名，用来根据对应的模版ID来生成对应的策略类对象
    private final String className;

    SubMessageTemplateEnum(String templateId, String className) {
        this.templateId = templateId;
        this.className = className;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getClassName() {
        return className;
    }

    public static Map<String, String> getAllClazz() {
        Map<String, String> map = new HashMap<>(4);
        for (SubMessageTemplateEnum value : SubMessageTemplateEnum.values()) {
            map.put(value.getTemplateId(), value.getClassName());
        }
        return map;
    }
}
~~~

> 策略类上下文：此类用于获取对应的订阅消息策略

~~~java
public class SubMessageStrategyContext {

    /**
     * 根据模版ID选择使用哪中方式执行订阅消息
     *
     * @param templateId 模版ID
     * @return 订阅消息策略模式接口
     */
    @SuppressWarnings("all")
    public static SubMessageStrategy getSubMessageStrategy(WeChatEnum templateId) {
        Map<String, String> allClazz = SubMessageTemplateEnum.getAllClazz();
        String clazz = allClazz.get(templateId.getValue());
        SubMessageStrategy strategy = null;
        try {
            strategy = (SubMessageStrategy) Class.forName(clazz).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            //日志处理
        }
        return strategy;
    }

    /**
     * 回信订阅消息策略实现类
     */
    @SuppressWarnings("unused")
    public static class ReplySubMessageStrategy implements SubMessageStrategy {

        @Override
        public void processSubMessage(SubMessageParam param, String... reserveParam) {
            String accessToken = GeneralDataProcessUtil.getAccessToken();
            Object data = new SubMessageReplyVo(
                    new SubMessageValueVo(param.getNickname())
                    , new SubMessageValueVo(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    , new SubMessageValueVo(param.getShowContent().length() > 20 ? param.getShowContent().substring(0, 20) : param.getShowContent()));
            String body = GeneralDataProcessUtil.packageSubMessageJson(data, param, reserveParam[0]);
            String post = HttpUtil.post(WeChatEnum.SUB_MESSAGE_SEND_URL_POST.getValue() + "?access_token=" + accessToken.replace("\"", ""), body);
            System.out.println(param.getNickname() + ":::" + param.getTouser() + post);
        }
    }

    /**
     * 评论订阅消息策略实现类
     */
    @SuppressWarnings("unused")
    public static class CommentSubMessageStrategy implements SubMessageStrategy {

        @Override
        public void processSubMessage(SubMessageParam param, String... reserveParam) {
            String accessToken = GeneralDataProcessUtil.getAccessToken();
            Object data = new SubMessageCommentDataVo(
                    new SubMessageValueVo(param.getShowContent().length() >= 20 ? param.getShowContent().substring(0, 20) : param.getShowContent())
                    , new SubMessageValueVo(param.getNickname())
                    , new SubMessageValueVo(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    , new SubMessageValueVo(param.getTitle().length() >= 20 ? param.getTitle().substring(0, 20) : param.getTitle()));
            String body = GeneralDataProcessUtil.packageSubMessageJson(data, param, "");
            String post = HttpUtil.post(WeChatEnum.SUB_MESSAGE_SEND_URL_POST.getValue() + "?access_token=" + accessToken.replace("\"", ""), body);
            System.out.println(param.getNickname() + ":::" + param.getTouser() + post);
        }
    }
}
~~~

> 定义完成策略需要的内容之后我们来进行实际使用
> client

~~~java
class Test {
    public static void main(String[] args) {
        //订阅消息
//封装参数
        SubMessageParam param = new SubMessageParam(letterReplyDto.getLetterId(), letterReplyDto.getMessage(), "", letterReplyDto.getSenderPenName(), letterReplyDto.getRecipient(), letterReplyDto.getSender(), letterReplyDto, WeChatEnum.SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID, WeChatEnum.SUB_MESSAGE_REPLY_PAGE, WeChatEnum.SUB_MESSAGE_MINI_PROGRAM_STATE_FORMAL_VERSION);
//获取对应的订阅消息策略对象然后处理
        SubMessageStrategyContext.getSubMessageStrategy(WeChatEnum.SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID).processSubMessage(param, replyId);


//评论订阅消息
        SpittingGrooves spittingGrooves = spittingGroovesMapper.selectOpenIdAndContentById(entity.getSgId());
        SubMessageParam param = SubMessageParam.builder()
                .parentId(entity.getSgId())
                .showContent(entity.getContent())
                .title(spittingGrooves.getContent())
                .touser(spittingGrooves.getOpenId())
                .sender(entity.getOpenId())
                .nickname(userMapper.selectPenNameByOpenId(entity.getOpenId()))
                .templateId(WeChatEnum.SUB_MESSAGE_COMMENT_TEMPLATE_ID)
                .version(WeChatEnum.SUB_MESSAGE_MINI_PROGRAM_STATE_FORMAL_VERSION).page(WeChatEnum.SUB_MESSAGE_COMMENT_PAGE).build();
        SubMessageStrategyContext.getSubMessageStrategy(WeChatEnum.SUB_MESSAGE_COMMENT_TEMPLATE_ID).processSubMessage(param);

    }
}

~~~

> 至此我们知道如果以后需要进行其他类型订阅消息只需要添加对应的策略实现就可以

## 二、`pipeline`设计模式

> `pipeline`设计模式非常容易理解，因为他本身就是一种管道设计模式，将我们原有的逻辑拆分为一个一个的算法，然后根据具体的业务需求组合使用一个个算法排列成一个管道执行
>
> 不过在具体业务中如何使用我们来具体看一下

### 前言

> **使用背景**：用户数据分析
>
> **使用原因**：本次中涉及到用户发布内容的数据分析：信件、日记、吐槽，但是只是权宜之计，并不可以完全确保之后仍旧是这样几种方式，所以此处定义管道设计

### 代码实现

> `context`类，上下文类，用于贯穿整个管道执行的流程，只是一个抽象类，其中存储内容非常简单，只是一些开始时间以及结束时间

~~~java
public abstract class Context {

    /**
     * 处理开始时间
     */
    private LocalDateTime startTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime endTime;

    /**
     * 获取数据名称
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
~~~

> `UserDataAnalysisContext`，用户数据分析上下文类

~~~java
public class UserDataAnalysisContext extends Context {

    /**
     * 用户 id
     */
    private String openId;


    /**
     * 当前时间
     */
    private String currentTime;

    /**
     * 用户回复信息以及历史分析数据
     */
    private ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> result;

    /**
     * 数据分析内容
     */
    private Map<String, List<ProfileNumVo>> analysis;

    /**
     * 模型创建出错时的错误信息
     */
    private String errorMsg;

    // 其他参数

    @Override
    public String getName() {
        return "用户数据分析";
    }
}
~~~

> 定义一个完成上下文，那么我们去定义一些算法，这些算法就是我们具体的一个个逻辑实现

~~~java
public interface ContextHandler<T extends Context> {

    /**
     * 处理输入的上下文数据
     *
     * @param context 处理时的上下文数据
     * @return 返回 true 则表示由下一个 ContextHandler 继续处理，返回 false 则表示处理结束
     */
    boolean handle(T context);
}

@Component
public class DiaryAnalysisContextHandler implements ContextHandler<UserDataAnalysisContext> {

    private final DiaryMapper diaryMapper;

    public DiaryAnalysisContextHandler(DiaryMapper diaryMapper) {
        this.diaryMapper = diaryMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<String> diaryContentList = diaryMapper.selectAllDiaryContentByOpenId(context.getOpenId(), context.getCurrentTime());
        List<ProfileNumVo> profileNumVos = GeneralDataProcessUtil.singleAnalysis(diaryContentList);
        return context.getAnalysis().put("diary", profileNumVos) == null;
    }
}

@Component
public class LetterAnalysisContextHandler implements ContextHandler<UserDataAnalysisContext> {

    private final LetterMapper letterMapper;

    public LetterAnalysisContextHandler(LetterMapper letterMapper) {
        this.letterMapper = letterMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<String> letterContentList = letterMapper.selectAllLetterContentByOpenId(context.getOpenId(), context.getCurrentTime());
        List<ProfileNumVo> profileNumVos = GeneralDataProcessUtil.singleAnalysis(letterContentList);
        return context.getAnalysis().put("letter", profileNumVos) == null;
    }
}

@Component
public class ReviewContextHandler implements ContextHandler<UserDataAnalysisContext> {


    private final LetterMapper letterMapper;

    public ReviewContextHandler(LetterMapper letterMapper) {
        this.letterMapper = letterMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<List<String>> review = new ArrayList<>();
        review.add(Arrays.asList("发布数量", "解忧", "日记", "吐槽"));
        //表示有多少列
        int countOfColumn = review.get(0).size();
        for (int i = 0; i < countOfColumn; i++) {
            review.add(Arrays.asList("第" + (i + 1) + "周"
                    , letterMapper.selectWeeklyQuantityThroughOpenId(context.getOpenId(), context.getCurrentTime(), "letter", i, 7) + ""
                    , letterMapper.selectWeeklyQuantityThroughOpenId(context.getOpenId(), context.getCurrentTime(), "diarys", i, 7) + ""
                    , letterMapper.selectWeeklyQuantityThroughOpenId(context.getOpenId(), context.getCurrentTime(), "spitting_grooves", i, 7) + "")
            );
        }
        return context.getResult().setFirstValue(review) != null;
    }
}


@Component
public class SpitAnalysisContextHandler implements ContextHandler<UserDataAnalysisContext> {

    private final SpittingGroovesMapper spittingGroovesMapper;

    public SpitAnalysisContextHandler(SpittingGroovesMapper spittingGroovesMapper) {
        this.spittingGroovesMapper = spittingGroovesMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<String> spittingGroovesContentList = spittingGroovesMapper.selectAllSpitContentByOpenId(context.getOpenId(), context.getCurrentTime());
        List<ProfileNumVo> profileNumVos = GeneralDataProcessUtil.singleAnalysis(spittingGroovesContentList);
        return context.getAnalysis().put("spit_groove", profileNumVos) == null;
    }
}
~~~

> 定义完成具体算法之后，我们需要去完成这样一件事情，既然前面已经说管道，那么必定有管道的一种体现方式，同时也是为了以后的扩展性，我们需要在这里定义一个路由表，用来记录都有哪些算法定义以及有哪些管道可以使用

~~~java
public class PipelineRouteConfig implements ApplicationContextAware {

    private ApplicationContext appContext;

    @Override
    @SuppressWarnings("all")
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    //真正的路由表实现，使用Map存储
    //key : Context的实现类Class对象
    //value : 一个个List，其中存储的就是处理器 ，也就是一个个管道
    private static final
    Map<Class<? extends Context>,
            List<Class<? extends ContextHandler<? extends Context>>>> PIPELINE_ROUTE_MAP = new HashMap<>(4);

    //使用静态代码块对Map进行初始化
    static {
        PIPELINE_ROUTE_MAP.put(UserDataAnalysisContext.class,
                //用户来数据分析等的管道，管道中内容有：回顾历史，信件、日记、吐槽内容分析
                Arrays.asList(
                        ReviewContextHandler.class,
                        LetterAnalysisContextHandler.class,
                        DiaryAnalysisContextHandler.class,
                        SpitAnalysisContextHandler.class
                ));
        // 将来其他 Context 的管道配置
    }

    /**
     * 路由表存储的是对应的Class对象，通过这个方法通过ApplicationContext获取到对应的实例化对象
     * @return 获取对应的Map
     */
    @Bean("pipelineRouteMap")
    public Map<Class<? extends Context>, List<? extends ContextHandler<? extends Context>>> getHandlerPipelineMap() {
        return PIPELINE_ROUTE_MAP.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::toPipeline));
    }

    /**
     * 根据给定的管道中 ContextHandler 的类型的列表，构建管道
     */
    private List<? extends ContextHandler<? extends Context>> toPipeline(
            Map.Entry<Class<? extends Context>, List<Class<? extends ContextHandler<? extends Context>>>> entry) {
        return entry.getValue()
                .stream()
                .map(appContext::getBean)
                .collect(Collectors.toList());
    }
}
~~~

> 定义完成之后，为了以后调用方便，所以定义一个处理器类然后对外提供一个执行访问入口，这样只需要去调用执行器的方法执行就可以，而不用去自己操作管道

~~~java
public class PipelineExecutor<R> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 引用 PipelineRouteConfig 中的 pipelineRouteMap
     */
    @Resource
    private Map<Class<? extends Context>,
            List<? extends ContextHandler<? super Context>>> pipelineRouteMap;

    /**
     * 同步处理输入的上下文数据<br/>
     * 如果处理时上下文数据流通到最后一个处理器且最后一个处理器返回 true，则返回 true，否则返回 false
     *
     * @param context 输入的上下文数据
     * @return 处理过程中管道是否畅通，畅通返回 true，不畅通返回 false
     */
    public boolean acceptSync(Context context) {
        Objects.requireNonNull(context, "上下文数据不能为 null");
        // 获取对应上下文Context的Class对象
        Class<? extends Context> dataType = context.getClass();
        // 通过上面获取到的Class对象从容器中获取到对应的管道
        List<? extends ContextHandler<? super Context>> pipeline = pipelineRouteMap.get(dataType);

        if (CollectionUtils.isEmpty(pipeline)) {
            logger.error("{} 的管道为空", dataType.getSimpleName());
            return false;
        }

        // 管道是否畅通
        boolean lastSuccess = true;

        //遍历管道中的内容进行顺序执行
        for (ContextHandler<? super Context> handler : pipeline) {
            try {
                // 当前处理器处理数据，并返回是否继续向下处理
                lastSuccess = handler.handle(context);
            } catch (Throwable ex) {
                lastSuccess = false;
                logger.error("[{}] 处理异常，handler={}", context.getName(), handler.getClass().getSimpleName(), ex);
            }

            // 不再向下处理
            if (!lastSuccess) {
                break;
            }
        }
        return lastSuccess;
    }

    private final ThreadPoolExecutor pipelineThreadPool =
            new ThreadPoolExecutor(4, 8, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    /**
     * 异步执行任务，但是异步是以管道为单位
     *
     * @param context  上下文
     * @param callback 回调方法
     */
    public boolean acceptAsync(Context context, BiConsumer<Context, Boolean> callback) {
        AtomicBoolean lastSuccess = new AtomicBoolean(true);
        pipelineThreadPool.execute(() -> {
            //注意我这里实际上调用的是同步方法，是因为管道之间是异步，但是管道内依旧是同步
            boolean success = acceptSync(context);

            if (callback != null) {
                callback.accept(context, success);
            }
            lastSuccess.set(success);
        });
        return lastSuccess.get();
    }
}
~~~

> 对此算是定义完成，具体调用
>
> `client`

~~~java
class Test {
    public static void main(String[] args) {
//定义一个执行器
        PipelineExecutor<ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>> executor = new PipelineExecutor<>();
//封装上下文对象
        UserDataAnalysisContext context = UserDataAnalysisContext.builder().openId(openId).currentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTimeStamp))).build();
        executor.acceptSync(context);
//获取执行结果
        ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> result = context.getResult();
    }
}
~~~

## 三、模版设计模式

### 前言

> 使用背景：获取三封信件的不断迭代
>
> 使用原因：获取算法不断完善，同时获取三封信件的方法不仅仅从数据库中获取，其中还涉及到其他内容，比如从redis获取等，所以编写了一个模版，然后去调用重写的重新获取逻辑算法

### 代码实现

> 模版

~~~java
//这里之所以使用抽象类，一是强制开发者对获取信件的算法具体实现，二是对其他放的复用
@Component
public abstract class AbstractLetterServiceImpl implements LetterService {
    /**
     * 获取信件的模版方法，不允许重写，对于获取具体三封信件的逻辑可以进行对customGetLettersByOpenId的方法重写
     * @param openId 用户 open id
     * @return 顺序获取三封信件
     */
    @SneakyThrows
    @Override
    public final List<IndexLetterDto> getLettersByOpenId(String openId) {
        return CompletableFuture.supplyAsync(() -> {
            /*
             * 1.从缓存获取此用户已经获取到的信件信息，包括获取时间、上次访问数据库时候的信件数量(最开始时候默认为0)
             *  - 读取数据库信件数量，如果大于上次访问的信件数量说明数据已经更新，那么读取最新的最后十条数据然后获取返回，不大于的话直接返回为null，
             *    随机生成3个数字nextInt(10)，用来从最新的十篇中抽取三篇
             * */
            String listKey = "IndexLetterDtoList";
            String letterInfoKey = "letter:" + openId + ":letter_info";
            Object o = redisUtil.get(letterInfoKey);
            if (o == null) {
                setLetterInitInfoByOpenId(openId);
            }
            String letterInfoJson = (String) redisUtil.get(letterInfoKey);
            JsonNode letterInfoJsonNode = JsonUtil.parseJson(letterInfoJson);
            String lastDate = letterInfoJsonNode.findPath("date").toString();
            String currentDate = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int totalNumber = letterMapper.selectTotalNumberNonSelf(openId);
            int visibleNumber = letterInfoJsonNode.findPath("visitNumber").asInt();
            if (lastDate.equals(currentDate) || visibleNumber >= totalNumber) {
                //此时说明当天已经访问过，所以不用再查询而是直接从缓存中获取,数据库数量大于缓存中的数量表示数据库已经更新，则重新获取，否则也不再重新获取，而是直接获取缓存中的信件数据
                @SuppressWarnings("unchecked") List<IndexLetterDto> indexLetterDtoList = (List<IndexLetterDto>) JSONObject.parse(letterInfoJsonNode.findPath(listKey).toString());
                return indexLetterDtoList;
            } else {
                List<IndexLetterDto> indexLetterDtoList = customGetLettersByOpenId(openId, totalNumber);
                updateRedisLetterInfo(letterInfoKey, totalNumber, currentDate, indexLetterDtoList);
                return indexLetterDtoList;
            }
        }).get();
    }

    /**
     * 此方法是自定义获取三封信件的方法，交给子类完成
     * @param openId 用户 open id
     * @param totalNumber 数据库信件的总数
     * @return 三封信件
     */
    protected abstract List<IndexLetterDto> customGetLettersByOpenId(String openId, int totalNumber);
}
~~~

> 实现类举例

~~~java

@Service
@Slf4j
public class UpgradeLetterServiceImpl extends AbstractLetterServiceImpl {

    public UpgradeLetterServiceImpl(LetterMapper letterMapper, RedisUtil redisUtil, ReplyMapper replyMapper, UserMapper userMapper) {
        super(letterMapper, redisUtil, replyMapper, userMapper);
    }

    /**
     * 这里需要做出优化
     * 1.优先根据标签获取信件，如何判断
     * - "letter:" + openId + ":letter_info"这个缓存中存储每个用户的喜爱(此用户回信的信标签)的对应标签数量
     * 获取两则占比最大的，然后这个表示最有可能推荐的，查询数据库时候按照标签进行分组，获取数据库中这个标签组中两片信件
     * - 如果上述条件获取的是两封，然后这个是获取其他不相干类型的一封，以来扩展
     * - 都要保证时间优先，也就是优先把最新的信件推送出去
     * - 随机获取，不可以排着数据库获取，而是随机获取，当然每日不可以重复
     * <p>
     * 1.从缓存中获取已经读取到的位置数据，用来判断数据库中的数据是否是最新的
     * <p>
     * <p>
     * - 设计一个算法计算阈值
     * - 添加权重比值判断到获取随机数中，在计算随机数时候将此因素考虑进去
     *
     * @param openId 用户 open id
     * @return 获取三封信件
     */
    @Override
    protected List<IndexLetterDto> customGetLettersByOpenId(String openId, int totalNumber) {
        //此时需要从数据库获取内容,随机三个数字获取数据库中最新的十条数据中的位置
        int gainLetterNumber = totalNumber == 1 || totalNumber == 2 ? totalNumber : 3;
        List<IndexLetterDto> indexLetterDtoList = new ArrayList<>(3);
        //用来解决生成随机数重复问题，以防出现相同的信件，当然如果数据库的数据比较少，进行randomInt+1之后还是会有重复
        int[] randomIntArray = getDifferentArray(totalNumber, gainLetterNumber);
        for (int i = 0; i < gainLetterNumber; i++) {
            int randomInt = randomIntArray[i];
            Letter letter = letterMapper.selectRandomLatestLetter(openId, randomInt % totalNumber, 1, 1);
            GeneralDataProcessUtil.configLetterDataFromSingleObject(letter, openId, indexLetterDtoList);
        }
        return indexLetterDtoList;
    }

    /**
     * 获取length个不同数字的数字
     *
     * @param surplusThreshold 求余的阈值
     * @param length           要获取的长度
     * @return 不同数字数组
     */
    private int[] getDifferentArray(int surplusThreshold, int length) {
        Random random = new Random();
        int[] differentArray = new int[length];
        for (int i = 0; i < differentArray.length; i++) {
            differentArray[i] = spinRandomNumberToNonExist(random.nextInt(surplusThreshold), surplusThreshold, i, differentArray);
        }
        return differentArray;
    }

    /**
     * 自旋直到产生没有出现过的数字
     *
     * @param randomNumber  随机数字
     * @param spinThreshold 自旋阈值，表示每次对哪个数字进行取余
     * @param length        已经有的数字长度
     * @param alreadyNumber 已经出现的数字
     * @return 自旋数字结果
     */
    private int spinRandomNumberToNonExist(int randomNumber, int spinThreshold, int length, int... alreadyNumber) {
        for (; ; ) {
            //成功标记，表示着是否已经不存在重复数字
            boolean successFlag = true;
            for (int i = 0; i < length; i++) {
                if (alreadyNumber[i] == randomNumber) {
                    randomNumber = (randomNumber + 1) % spinThreshold;
                    successFlag = false;
                }
            }
            if (successFlag) {
                return randomNumber;
            }
        }
    }
}
~~~

> 整体来看，模版设计方法比较简单，没有涉及其他的内容，只是为了更好的扩展性

## 四、观察者模式

### 前言

> 使用背景：用户成就邮票，监听用户成就与邮票的获取
>
> 使用原因：将此业务从正常的业务逻辑抽离，使用spring提供的观察者实现

### 代码实现

> 事件类型Event

~~~java
//此类存储用户触发事件的消息
public class PublishLetterEvent extends ApplicationEvent {

    private final LetterStorageDto letterStorageDto;

    public PublishLetterEvent(Object source, LetterStorageDto letterStorageDto) {
        super(source);
        this.letterStorageDto = letterStorageDto;
    }

    public LetterStorageDto getLetterStorageDto() {
        return letterStorageDto;
    }
}
//事件类还有其他不再一一列举
~~~

> 事件监听处理类

~~~java

@Configuration
public class CustomListenerConfig {
    /**
     * 处理保存信件事件
     *
     * @param letterEvent 信件事件
     */
    @EventListener
    public void handleSaveLetterEvent(PublishLetterEvent letterEvent) {
        LOG.info("触发保存信件事件，此时应该更新关于信件的成就内容");
        LOG.info(letterEvent.getLetterStorageDto().toString());
        EXECUTOR.execute(getRunnable(letterEvent.getLetterStorageDto().getOpenId(), "letter", letterEvent.getLetterStorageDto().getContent()));
    }
}
~~~

> 发布事件

~~~java
class Test {
    public static void main(String[] args) {
        publisher.publishEvent(new PublishLetterEvent(this, letterStorageDto));
    }
}
~~~

> 基于spring提供的事件监听机制，spring也是在JDK 提供的事件监听基础上做了二次封装
