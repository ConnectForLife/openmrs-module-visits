/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from "react";
import ReactTable from "react-table";
import { LocalizedMessage } from "@openmrs/react-components";
import { DEFAULT_ITEMS_PER_PAGE, DEFAULT_SORT, DEFAULT_ORDER, MIN_ROWS, PAGE_SIZE_OPTIONS } from "./constants";
import _ from "lodash";

interface IPaginationBaseState {
  itemsPerPage: number;
  sort: string;
  order: string;
  activePage: number;
}

export interface ITableProps {
  query?: string;
  filters: {};
  data: any[];
  columns: any[];
  loading: boolean;
  pages: number;
  showPagination?: boolean;
  sortable?: boolean;
  multiSort?: boolean;
  resizable?: boolean;
  noDataText?: string;
  fetchDataCallback(
    activePage: number,
    itemsPerPage: number,
    sort: string,
    order: string,
    filters?: {},
    query?: string
  ): void;
  onRowClick(rowEntity: {}): void;
}

export default class OverviewVisitTable extends React.PureComponent<ITableProps, IPaginationBaseState> {
  constructor(props) {
    super(props);
    this.state = {
      activePage: 0,
      itemsPerPage: DEFAULT_ITEMS_PER_PAGE,
      sort: DEFAULT_SORT,
      order: DEFAULT_ORDER,
    };
  }

  componentDidUpdate(prevProps) {
    const { activePage, itemsPerPage, sort, order } = this.state;
    const { filters, query } = this.props;
    if (prevProps.query !== query || prevProps.filters !== filters) {
      this.props.fetchDataCallback(activePage, itemsPerPage, sort, order, filters, query);
    }
  }

  fetchData = (state, instance) => {
    this.setState(
      {
        activePage: state.page,
        itemsPerPage: state.pageSize,
        sort: state.sorted[0] ? state.sorted[0].id : DEFAULT_SORT,
        order: state.sorted[0] ? (state.sorted[0].desc ? "desc" : "asc") : DEFAULT_ORDER,
      },
      () =>
        this.props.fetchDataCallback(
          this.state.activePage,
          this.state.itemsPerPage,
          this.state.sort,
          this.state.order,
          this.props.filters,
          this.props.query
        )
    );
  };

  render = () => {
    const NullComponent = () => null;
    const noDataText = <LocalizedMessage id="reactcomponents.table.noDataText" defaultMessage="No results found" />;
    const previousText = <LocalizedMessage id="reactcomponents.table.previous" defaultMessage="Previous" />;
    const nextText = <LocalizedMessage id="reactcomponents.table.next" defaultMessage="Next" />;
    const loadingText = <LocalizedMessage id="reactcomponents.table.loading" defaultMessage="Loading..." />;
    const pageText = <LocalizedMessage id="reactcomponents.table.page" defaultMessage="Page" />;
    const ofText = <LocalizedMessage id="reactcomponents.table.of" defaultMessage="of" />;
    const rowsText = "results";

    return (
      <ReactTable
        className="-striped -highlight"
        collapseOnDataChange={false}
        columns={this.props.columns}
        data={this.props.data}
        defaultPageSize={DEFAULT_ITEMS_PER_PAGE}
        manual={true}
        loading={this.props.loading}
        minRows={MIN_ROWS}
        pages={this.props.pages}
        pageSizeOptions={PAGE_SIZE_OPTIONS}
        onFetchData={this.fetchData}
        multisort={false}
        nextText={nextText}
        previousText={previousText}
        rowsText={rowsText}
        loadingText={loadingText}
        ofText={ofText}
        noDataText={_.get(this.props, "noDataText", <span className="sortableTable-noDataText">{noDataText}</span>)}
        NoDataComponent={NullComponent}
        pageText={pageText}
        showPagination={_.get(this.props, "showPagination", true)}
        sortable={_.get(this.props, "sortable", true)}
        multiSort={_.get(this.props, "multiSort", true)}
        resizable={_.get(this.props, "resizable", true)}
        getTrProps={(_, rowInfo) => ({
          onClick: () => this.props.onRowClick(rowInfo.original)
        })}
      />
    );
  };
}
