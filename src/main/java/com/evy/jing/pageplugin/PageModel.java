package com.evy.jing.pageplugin;

import java.io.Serializable;

/**
 * 分页类
 */
public class PageModel implements Serializable {
    /**
     * 每页显示页数
     */
    private int showCount = 3;
    /**
     * 总页数
     */
    private int totalCount;
    /**
     * 当前页
     * 最小值为1
     */
    private int currentPage = 1;
    /**
     * limit第一个参数，也就是 (currentPage - 1) * showCount
     */
    private int currentResult;

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentResult() {
        return (getCurrentPage() - 1) * getShowCount();
    }

    public void setCurrentResult(int currentResult) {
        this.currentResult = currentResult;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "showCount=" + showCount +
                ", totalCount=" + totalCount +
                ", currentPage=" + currentPage +
                ", currentResult=" + currentResult +
                '}';
    }
}
