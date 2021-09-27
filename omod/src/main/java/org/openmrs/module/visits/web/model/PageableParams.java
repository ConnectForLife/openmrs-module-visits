package org.openmrs.module.visits.web.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.visits.api.exception.ValidationException;
import org.openmrs.module.visits.domain.PagingInfo;

/**
 * Models the messaging controller parameters
 */
public class PageableParams extends AbstractDTO {

    private static final long serialVersionUID = 8249775572545602184L;

    /**
     * The default page to display.
     */
    private static final int DEFAULT_PAGE_INDEX = 1;

    /**
     * The default value of number of rows to display per page.
     */
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * The number of rows to display per page.
     */
    private Integer rows;

    /**
     * The page to display, starting from 1.
     */
    private Integer page;

    /**
     * Gets {@link PagingInfo} containing the paging configuration form grid settings
     * @return the newly created paging information
     */
    public PagingInfo getPagingInfo() {
        Integer pageIndex = getPage();
        validatePageIndex(pageIndex);

        Integer pageSize = getRows();
        validatePageSize(pageSize);

        return new PagingInfo(pageIndex != null ? pageIndex : DEFAULT_PAGE_INDEX,
            pageSize != null ? pageSize : DEFAULT_PAGE_SIZE);
    }

    public Integer getRows() {
        return rows;
    }

    public PageableParams setRows(Integer rows) {
        this.rows = rows;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public PageableParams setPage(Integer page) {
        this.page = page;
        return this;
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

    private void validatePageIndex(Integer pageIndex) {
        if (pageIndex != null && pageIndex <= 0) {
            throw new ValidationException(String.format("Invalid page index %s", pageIndex));
        }
    }

    private void validatePageSize(Integer pageSize) {
        if (pageSize != null && pageSize <= 0) {
            throw new ValidationException(String.format("Invalid page size %s", pageSize));
        }
    }
}
