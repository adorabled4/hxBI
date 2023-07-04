package com.dhx.bi.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author adorabled4
 * @className SqlUtils
 * @date : 2023/07/04/ 10:35
 **/
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
