export type ShippingMethod =
  | "STANDARD"
  | "EXPRESS";

export type PaymentMethod =
  | "CARD"
  | "PAYPAL";

export interface CheckoutRequest {
  addressId: number;
  shippingMethod: ShippingMethod;
  paymentMethod: PaymentMethod;
}

export interface OrderItem {
  id: number;
  sku: string;
  productName: string;

  size: string | null;
  color: string | null;
  style: string | null;
  material: string | null;

  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface OrderResponse {
  id: number;

  orderNumber: string;

  status: string;

  shippingMethod: ShippingMethod;
  paymentMethod: PaymentMethod;
  paymentStatus: string;

  subtotal: number;
  discountAmount: number;
  giftCardAmount: number;
  shippingAmount: number;
  taxAmount: number;
  totalAmount: number;

  recipientName: string;

  addressLine1: string;
  addressLine2: string | null;

  city: string;
  state: string;
  postalCode: string;
  countryCode: string;

  phone: string;

  items: OrderItem[];

  createdAt: string;
}