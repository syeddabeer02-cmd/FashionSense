export interface Product {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  basePrice: number;
  active: boolean;
  brandName?: string;
  brandSlug?: string;
  categoryName?: string;
  categorySlug?: string;
}

export interface ProductPage {
  content: Product[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}