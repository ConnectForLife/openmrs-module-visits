package org.openmrs.module.visits.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.visits.domain.PagingInfo;

/**
 * Represent the DTO for the specific Page resource
 * @param <T> - related type of resource
 */
public class PageDTO<T> {

    private int pageIndex;

    private int pageSize;

    private int contentSize;

    private Long totalRecords;

    private List<T> content = new ArrayList<T>();

    private int pageCount;

    public PageDTO() { }

    public PageDTO(List<T> content, PagingInfo pagingInfo) {
        createContentInfo(content);
        createPageInfo(pagingInfo);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public PageDTO<T> setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageDTO<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getContentSize() {
        return contentSize;
    }

    public PageDTO<T> setContentSize(int contentSize) {
        this.contentSize = contentSize;
        return this;
    }

    public List<T> getContent() {
        return content;
    }

    public PageDTO<T> setContent(List<T> content) {
        this.content = content;
        return this;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public PageDTO<T> setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public Integer getPageCount() {

        if (totalRecords == null) {
            pageCount = 0;
        } else {
            pageCount = (int) Math.ceil((double) totalRecords / (double) pageSize);
        }
        return pageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    private void createContentInfo(List<T> content) {
        if (content != null) {
            this.content = content;
            this.contentSize = content.size();
        }
    }

    private void createPageInfo(PagingInfo pagingInfo) {
        if (pagingInfo != null) {
            this.pageIndex = pagingInfo.getPage();
            this.pageSize = pagingInfo.getPageSize();
            this.totalRecords = pagingInfo.getTotalRecordCount();
        }
    }
}
