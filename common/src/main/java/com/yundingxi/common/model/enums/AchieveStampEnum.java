package com.yundingxi.common.model.enums;

/**
 * 对于成就邮票他们的hash code应该这样去理解
 * partition 0  1 2 3 4 5 6 7
 * type      l  d s r l d s r
 * 也就是有这样轮训的方式
 * @version v1.0
 * @ClassName AchieveStampEnum
 * @Author rayss
 * @Datetime 2022/4/18 10:55 上午
 */

public enum AchieveStampEnum {
    /**
     * 信件类型，这里信件对应Hashcode设置为0
     */
    LETTER_TYPE("letter", 0),
    /**
     * 日记类型，这里信件对应Hashcode设置为1
     */
    DIARY_TYPE("diary", 1),
    /**
     * 吐槽类型，这里信件对应Hashcode设置为2
     */
    SPIT_TYPE("spit", 2),
    /**
     * 回信类型，这里信件对应Hashcode设置为0
     */
    REPLY_TYPE("reply", 3);

    /**
     * 类型
     */
    private final String groupId;
    /**
     * 类型对应的hash code值
     */
    private final int partitionIndex;

    AchieveStampEnum(String groupId, int partitionIndex){
        this.groupId = groupId;
        this.partitionIndex = partitionIndex;
    }

    public String getGroupId() {
        return groupId;
    }

    public int getPartitionIndex() {
        return partitionIndex;
    }
}
