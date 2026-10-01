import type {
  ProductSummary,
} from "@/types/product-detail";

export interface WishlistItem {
  wishlistItemId: number;

  product: ProductSummary;

  addedAt: string;
}