import {ObjectUI} from "@bit/soldevelo-omrs.cfl-components.base-model";
import {IForm} from "@bit/soldevelo-omrs.cfl-components.validation/model/form";
import * as Yup from "yup";
import _ from 'lodash';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import {validateFormSafely} from '@bit/soldevelo-omrs.cfl-components.validation';
import IVisitDetails from "./visit-details";

export default class VisitUI extends ObjectUI<IVisitDetails> implements IVisitDetails, IForm {
  uuid: string;
  startDate: Date;
  time: string | null;
  location: string | null;
  type: string;
  status: string | null;
  actualDate?: Date | undefined;

  errors: { [key: string]: string; };
  touchedFields: { [key: string]: boolean; };

  constructor(baseObject: IVisitDetails) {
    super(baseObject);
    this.errors = {};
    this.touchedFields = {};
  }

  async validate(validateNotTouched: boolean, isEdit: boolean): Promise<VisitUI> {
    const schema = this.getValidationSchema(validateNotTouched, isEdit);

    const validationResult = await validateFormSafely(this, schema);

    const visit = _.clone(this);
    visit.errors = validationResult;
    return visit;
  }

  getValidationSchema(validateNotTouched: boolean, isEdit: boolean): Yup.ObjectSchema {
    const createValidators = {
      type: Yup.string().test('mandatory check', getIntl().formatMessage({ id: 'VISITS_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED }),
        v => this.validateRequiredField('type', v, validateNotTouched))
    };
    const editValidators = {
      type: Yup.string().test('mandatory check', getIntl().formatMessage({ id: 'VISITS_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED }),
        v => this.validateRequiredField('type', v, validateNotTouched)),
      status: Yup.string().test('mandatory check', getIntl().formatMessage({ id: 'VISITS_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED }),
        v => this.validateRequiredField('status', v, validateNotTouched))
    };

    return Yup.object().shape(isEdit? editValidators : createValidators);
  }

  validateRequiredField(key: string, value: string, validateNotTouched: boolean): boolean {
    if (this.touchedFields[key] || validateNotTouched) {
      return !!(value && value.trim());
    }
    return true;
  }

  static getNew(): VisitUI {
    return new VisitUI({
      startDate: new Date()
    } as IVisitDetails);
  }

}
