export interface ProductSummary {
  id: number;
  name: string;
  slug: string;
  description: string;
  basePrice: number;
  active: boolean;

  brandId: number;
  brandName: string;
  brandSlug: string;

  categoryId: number;
  categoryName: string;
  categorySlug: string;

  occasions: string[];

  createdAt: string;
  updatedAt: string;
}

export interface ProductVariant {
  id: number;
  productId: number;
  sku: string;
  size: string;
  color: string;
  style: string;
  material: string;
  price: number;
  availability: "IN_STOCK" | "OUT_OF_STOCK";
  active: boolean;
}

export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
  altText: string;
  displayOrder: number;
  primaryImage: boolean;
}

export interface ProductDetailResponse {
  product: ProductSummary;
  variants: ProductVariant[];
  images: ProductImage[];
}