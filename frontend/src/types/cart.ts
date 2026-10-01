export interface CartItem {
  cartItemId: number;
  variantId: number;
  sku: string;

  productName: string;
  productSlug: string;

  size: string;
  color: string;
  style: string;
  material: string;

  unitPrice: number;
  quantity: number;
  lineTotal: number;

  availability:
    | "IN_STOCK"
    | "OUT_OF_STOCK";
}

export interface CartResponse {
  cartId: number;
  items: CartItem[];
  subtotal: number;
}