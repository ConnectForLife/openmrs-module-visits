import { ISessionLocation } from "./session-location.model";

export interface ISession {
  authenticated: boolean;
  locale: string;
  currentProvider: any;
  sessionLocation: ISessionLocation;
}
