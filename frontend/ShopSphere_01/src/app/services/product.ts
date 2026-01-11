// src/app/services/product.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product, CustomOptionGroup } from '../models/product';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ProductResponseDTO {
  id: number;
  name: string;

  price: number;
  description: string;
  imageUrl: string;
  categoryName: string;
  stockLevel: number;
  isActive: boolean;
  reorderThreshold: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) { }

  /**
   * Get all products from backend (/api/products).
   * Maps ProductResponseDTO to Product model.
   * Injects default customOptions as backend does not store them yet.
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<ProductResponseDTO[]>(this.apiUrl).pipe(
      map(products => products.map(product => this.mapDTOToProduct(product)))
    );
  }

  /**
   * Add a new product to backend.
   */
  addProduct(
    partial: Omit<Product, 'productId'> & { productId?: string; categoryId?: number; categoryName?: string }
  ): Observable<Product> {
    const payload = {
      name: partial.name,
      description: partial.description,
      price: partial.basePrice,
      imageUrl: partial.previewImage,
      categoryName: partial.categoryName,
      stockLevel: partial.stockLevel || 0,

      isActive: partial.isActive !== undefined ? partial.isActive : true,
      reorderThreshold: partial.reorderThreshold || 0
    };

    return this.http.post<ProductResponseDTO>(this.apiUrl, payload).pipe(
      map(product => this.mapDTOToProduct(product))
    );
  }

  getProductById(productId: string): Observable<Product | undefined> {
    return this.http.get<ProductResponseDTO>(`${this.apiUrl}/${productId}`).pipe(
      map(product => this.mapDTOToProduct(product))
    );
  }

  /** Update an existing product */
  updateProduct(product: Product): Observable<Product> {
    if (!product.productId) {
      throw new Error('Product ID is required to update a product');
    }

    const payload = {
      name: product.name,
      description: product.description,
      price: product.basePrice,
      imageUrl: product.previewImage,
      categoryName: product.category,
      stockLevel: product.stockLevel,

      isActive: product.isActive,
      reorderThreshold: product.reorderThreshold
    };

    return this.http.put<ProductResponseDTO>(`${this.apiUrl}/${product.productId}`, payload).pipe(
      map(p => this.mapDTOToProduct(p))
    );
  }

  /** Delete a product */
  deleteProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${productId}`);
  }

  /** Update product stock level */
  updateStock(productId: string, stockLevel: number): Observable<Product> {
    // Only update stockLevel
    return this.getProductById(productId).pipe(
      map(product => {
        if (!product) throw new Error('Product not found');
        // This is a client side simulation of update, ideally backend has PATCH
        return { ...product, stockLevel };
      })
    );
  }

  // Helper to map DTO to Model
  private mapDTOToProduct(dto: ProductResponseDTO): Product {
    return {
      productId: dto.id.toString(),
      name: dto.name,
      description: dto.description,
      basePrice: dto.price,
      previewImage: dto.imageUrl,
      category: dto.categoryName,
      stockLevel: dto.stockLevel,
      isActive: dto.isActive,
      reorderThreshold: dto.reorderThreshold,

      customOptions: this.getDefaultCustomOptions()
    };
  }

  // ---- default custom options (used if db.json doesn't define them) ----
  private getDefaultCustomOptions(): CustomOptionGroup[] {
    return [
      {
        type: 'colour',
        values: ['Silver', 'Gold', 'Rose Gold'],
        priceAdjustment: {
          Silver: 0,
          Gold: 15,
          'Rose Gold': 10
        }
      },
      {
        type: 'size',
        values: ['Small', 'Medium', 'Large'],
        priceAdjustment: {
          Small: -5,
          Medium: 0,
          Large: 10
        }
      },
      {
        type: 'material',
        values: ['Standard', 'Premium'],
        priceAdjustment: {
          Standard: 0,
          Premium: 25
        }
      }
    ];
  }

  /** Update reorder threshold */
  updateReorderThreshold(productId: string, reorderThreshold: number): Observable<Product> {
    return this.getProductById(productId).pipe(
      map(p => {
        if (!p) throw new Error("Product not found");
        return { ...p, reorderThreshold };
      })
    );
  }
}
