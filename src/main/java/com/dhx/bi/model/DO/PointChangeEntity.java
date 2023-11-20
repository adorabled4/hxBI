package com.dhx.bi.model.DO;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.dhx.bi.model.enums.PointChangeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName point_changes
 */
@TableName(value = "point_changes")
@Data
@AllArgsConstructor // 如果自己新写了构造器, 一定要加上这个注解!!! , 否则查询sql返回构造对象的时候会用自己写的构造器
@NoArgsConstructor
public class PointChangeEntity implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分变动值
     */
    private Integer changeAmount;

    /**
     * 积分变动类型：增加/减少
     */
    private Integer changeType;

    /**
     * 变动原因
     */
    private String reason;

    /**
     * 变动后的新积分数量
     */
    private Integer newPoints;

    /**
     * 变动来源
     */
    private String source;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public PointChangeEntity(PointChangeEnum pointChangeEnum, long userId) {
        this.changeAmount = pointChangeEnum.getChangeAmount();
        this.source = pointChangeEnum.getSource();
        this.reason = pointChangeEnum.getReason();
        this.createTime = LocalDateTime.now();
        this.userId = userId;
        this.changeType = pointChangeEnum.getChangeType().ordinal();
    }

    /**
     * 积分变更类型
     *
     * @author dhx
     * @date 2023/11/19
     */


    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PointChangeEntity other = (PointChangeEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getChangeAmount() == null ? other.getChangeAmount() == null : this.getChangeAmount().equals(other.getChangeAmount()))
                && (this.getChangeType() == null ? other.getChangeType() == null : this.getChangeType().equals(other.getChangeType()))
                && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()))
                && (this.getNewPoints() == null ? other.getNewPoints() == null : this.getNewPoints().equals(other.getNewPoints()))
                && (this.getSource() == null ? other.getSource() == null : this.getSource().equals(other.getSource()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getChangeAmount() == null) ? 0 : getChangeAmount().hashCode());
        result = prime * result + ((getChangeType() == null) ? 0 : getChangeType().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
        result = prime * result + ((getNewPoints() == null) ? 0 : getNewPoints().hashCode());
        result = prime * result + ((getSource() == null) ? 0 : getSource().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", changeAmount=").append(changeAmount);
        sb.append(", changeType=").append(changeType);
        sb.append(", reason=").append(reason);
        sb.append(", newPoints=").append(newPoints);
        sb.append(", source=").append(source);
        sb.append(", createTime=").append(createTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}