/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import _ from 'lodash';
import { Modal, Button } from 'react-bootstrap';
import { LocalizedMessage } from '@openmrs/react-components';

import IModalParams from './modal-params';
import './delete-visit-modal.scss';

interface IProps {
  show: boolean,
  modalParams: IModalParams | null,
  confirm: (modalParams: IModalParams | null) => void,
  cancel: () => void
}

interface IState {
}

class DeleteVisitModal extends React.PureComponent<IProps, IState> {

  buildModal = (modalParams: IModalParams) => {
    const { show, confirm, cancel } = this.props;
    const title = <LocalizedMessage
      id="managevisits.modal.title"
      defaultMessage="Delete Visit" />;
    const txt = <LocalizedMessage
      id="managevisits.modal.txt"
      defaultMessage="Are you sure you want to delete this Visit?" />;
    const confirmLabel = <LocalizedMessage
      id="managevisits.modal.confirmLabel"
      defaultMessage="YES" />;
    const cancelLabel = <LocalizedMessage
      id="managevisits.modal.cancelLabel"
      defaultMessage="NO" />;

    return (
      <Modal id="delete-visit-modal" show={show} onHide={cancel}>
        <Modal.Body>
          <div className="modal-title">{title}</div>
          <p>{txt}</p>
          <Button
            bsClass="button confirm right"
            onClick={() => {
              confirm(modalParams);
            }}
          >
            {confirmLabel}
          </Button>
          <Button bsClass="button cancel" onClick={this.props.cancel}>
            {cancelLabel}
          </Button>
        </Modal.Body>
      </Modal>
    );
  }

  render() {
    return (
      this.props.show && !!this.props.modalParams
        ? this.buildModal(this.props.modalParams)
        : null
    );
  };
}

export default DeleteVisitModal;
