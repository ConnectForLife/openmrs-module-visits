export default interface IVisitDetails {
  uuid: string;
  startDate: Date;
  time: string | null;
  location: string | null;
  locationName?: string;
  type: string;
  typeName?: string;
  status: string | null;
  actualDate?: Date;
}
