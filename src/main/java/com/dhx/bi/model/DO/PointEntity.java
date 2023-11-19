package com.dhx.bi.model.DO;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @TableName points
 */
@TableName(value = "points")
@Data
public class PointEntity implements Serializable {
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
     * 剩余积分数量
     */
    private Integer remainingPoints;

    /**
     * 总积分数量
     */
    private Integer totalPoints;

    /**
     * 积分状态：有效/过期
     */
    private Integer status;

    /**
     * 最后操作时间
     */
    private LocalDateTime lastOperationTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 0
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public enum Status {
        VALID,
        EXPIRED
    }

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
        PointEntity other = (PointEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getRemainingPoints() == null ? other.getRemainingPoints() == null : this.getRemainingPoints().equals(other.getRemainingPoints()))
                && (this.getTotalPoints() == null ? other.getTotalPoints() == null : this.getTotalPoints().equals(other.getTotalPoints()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getLastOperationTime() == null ? other.getLastOperationTime() == null : this.getLastOperationTime().equals(other.getLastOperationTime()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getRemainingPoints() == null) ? 0 : getRemainingPoints().hashCode());
        result = prime * result + ((getTotalPoints() == null) ? 0 : getTotalPoints().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getLastOperationTime() == null) ? 0 : getLastOperationTime().hashCode());
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
        sb.append(", remainingPoints=").append(remainingPoints);
        sb.append(", totalPoints=").append(totalPoints);
        sb.append(", status=").append(status);
        sb.append(", lastOperationTime=").append(lastOperationTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}