export interface Address {
  id: number;
  label: string;
  recipientName: string;
  addressLine1: string;
  addressLine2: string | null;
  city: string;
  state: string;
  postalCode: string;
  countryCode: string;
  phone: string;
  defaultAddress: boolean;
}
