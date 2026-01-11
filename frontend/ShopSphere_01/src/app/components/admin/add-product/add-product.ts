// src/app/components/admin/add-product/add-product.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product';

@Component({
    selector: 'app-admin-add-product',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './add-product.html',
    styleUrls: ['./add-product.css']
})
export class AdminAddProduct {
    newProduct = {
        name: '',
        description: '',
        category: '',
        price: 0,
        imageUrl: '',
        stockLevel: 0,
        reorderThreshold: 0
    };

    selectedFileName = '';
    imagePreview = '';
    showSuccessMessage = false;

    constructor(private productService: ProductService) { }

    onFileSelected(event: any): void {
        const file = event.target.files[0];
        if (file) {
            this.selectedFileName = file.name;

            // 1. Create local preview (Base64) so user sees image immediately
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.imagePreview = e.target.result;
            };
            reader.readAsDataURL(file);

            // 2. Prepare payload path (Backend expects string)
            this.newProduct.imageUrl = `assets/images/products/${file.name}`;
        }
    }

    onAddProduct(): void {
        if (!this.newProduct.name || !this.newProduct.price || !this.newProduct.category) {
            return;
        }

        const productImage = this.newProduct.imageUrl || 'assets/images/placeholder.jpg';

        this.productService
            .addProduct({
                name: this.newProduct.name,
                description: this.newProduct.description,
                categoryName: this.newProduct.category, // Send Name instead of ID
                basePrice: Number(this.newProduct.price),
                previewImage: this.newProduct.imageUrl,
                stockLevel: Number(this.newProduct.stockLevel),
                reorderThreshold: Number(this.newProduct.reorderThreshold),
                customOptions: [],
                isActive: true
            })
            .subscribe({
                next: () => {
                    this.showSuccessMessage = true;
                    this.resetForm();

                    // Hide success message after 3 seconds
                    setTimeout(() => {
                        this.showSuccessMessage = false;
                    }, 3000);
                },
                error: (err: any) => {
                    console.error('Failed to add product', err);
                    alert('Failed to add product');
                }
            });
    }

    private resetForm(): void {
        this.newProduct = {
            name: '',
            description: '',
            category: '',
            price: 0,
            imageUrl: '',
            stockLevel: 0,
            reorderThreshold: 0
        };
        this.selectedFileName = '';
        this.imagePreview = '';

        const fileInput = document.getElementById('productImage') as HTMLInputElement;
        if (fileInput) {
            fileInput.value = '';
        }
    }
}
