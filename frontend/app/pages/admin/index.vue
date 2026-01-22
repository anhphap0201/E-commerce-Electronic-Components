<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Quản lý cửa hàng</h1>
        <p class="text-gray-600">Quản lý sản phẩm, danh mục và đơn hàng</p>
      </div>

      <!-- Tabs -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 mb-6">
        <div class="border-b border-gray-200">
          <nav class="flex -mb-px">
            <button
              @click="activeTab = 'products'"
              :class="[
                'flex items-center gap-2 px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === 'products'
                  ? 'border-[#09f] text-[#09f]'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
              Sản phẩm
              <span class="ml-2 px-2 py-0.5 text-xs font-semibold bg-gray-100 text-gray-700 rounded-full">
                {{ products.length }}
              </span>
            </button>
            <button
              @click="activeTab = 'categories'"
              :class="[
                'flex items-center gap-2 px-6 py-4 text-sm font-medium border-b-2 transition-colors',
                activeTab === 'categories'
                  ? 'border-[#09f] text-[#09f]'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              ]"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              Danh mục
              <span class="ml-2 px-2 py-0.5 text-xs font-semibold bg-gray-100 text-gray-700 rounded-full">
                {{ categories.length }}
              </span>
            </button>
          </nav>
        </div>
      </div>

      <!-- Products Tab -->
      <div v-if="activeTab === 'products'" class="space-y-6">
        <!-- Products Header -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative flex-1 min-w-[300px]">
              <input
                v-model="productSearch"
                type="text"
                placeholder="Tìm kiếm sản phẩm..."
                class="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
              />
              <svg class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
          <button
            @click="openProductModal()"
            class="flex items-center gap-2 px-4 py-2 bg-[#09f] text-white rounded-lg hover:bg-[#0077cc] transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Thêm sản phẩm
          </button>
        </div>

        <!-- Products Table -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sản phẩm</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Danh mục</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giá</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tồn kho</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
                  <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="product in filteredProducts" :key="product.id" class="hover:bg-gray-50 transition-colors">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <img :src="product.imageUrl || '/images/placeholder.png'" :alt="product.name" class="w-12 h-12 rounded-lg object-cover" />
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">{{ product.name }}</div>
                        <div class="text-sm text-gray-500 truncate max-w-xs">{{ product.description }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full">
                      {{ getCategoryName(product.category) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ formatPrice(product.price) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ product.stock }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'px-2 py-1 text-xs font-medium rounded-full',
                      product.stock > 0 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    ]">
                      {{ product.stock > 0 ? 'Còn hàng' : 'Hết hàng' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      @click="openProductModal(product)"
                      class="text-[#09f] hover:text-[#0077cc] mr-3"
                    >
                      Sửa
                    </button>
                    <button
                      @click="deleteProduct(product.id)"
                      class="text-red-600 hover:text-red-800"
                    >
                      Xóa
                    </button>
                  </td>
                </tr>
                <tr v-if="filteredProducts.length === 0">
                  <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                    <div class="flex flex-col items-center">
                      <svg class="w-12 h-12 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                      </svg>
                      Không tìm thấy sản phẩm nào
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Categories Tab -->
      <div v-if="activeTab === 'categories'" class="space-y-6">
        <!-- Categories Header -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative flex-1 min-w-[300px]">
              <input
                v-model="categorySearch"
                type="text"
                placeholder="Tìm kiếm danh mục..."
                class="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
              />
              <svg class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
          <button
            @click="openCategoryModal()"
            class="flex items-center gap-2 px-4 py-2 bg-[#09f] text-white rounded-lg hover:bg-[#0077cc] transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Thêm danh mục
          </button>
        </div>

        <!-- Categories Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="category in filteredCategories"
            :key="category.id"
            class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow"
          >
            <div v-if="category.imageUrl" class="h-32 overflow-hidden">
              <img :src="category.imageUrl" :alt="category.name" class="w-full h-full object-cover" />
            </div>
            <div class="p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ category.name }}</h3>
              <p class="text-sm text-gray-600 mb-4">{{ category.description }}</p>
              <div class="flex items-center justify-between">
                <span class="text-xs text-gray-500">
                  {{ getProductCountByCategory(category.id) }} sản phẩm
                </span>
                <div class="flex items-center gap-2">
                  <button
                    @click="openCategoryModal(category)"
                    class="p-2 text-[#09f] hover:bg-blue-50 rounded-lg transition-colors"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button
                    @click="deleteCategory(category.id)"
                    class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="filteredCategories.length === 0" class="col-span-full">
            <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
              <svg class="w-12 h-12 text-gray-400 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              <p class="text-gray-500">Không tìm thấy danh mục nào</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Product Modal -->
    <Teleport to="body">
      <Transition
        enter-active-class="transition-opacity duration-300 ease-out"
        leave-active-class="transition-opacity duration-200 ease-in"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div
          v-if="showProductModal"
          class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
          @click.self="closeProductModal"
        >
          <Transition
            enter-active-class="transition-all duration-300 ease-out"
            leave-active-class="transition-all duration-200 ease-in"
            enter-from-class="opacity-0 scale-95 translate-y-4"
            enter-to-class="opacity-100 scale-100 translate-y-0"
            leave-from-class="opacity-100 scale-100 translate-y-0"
            leave-to-class="opacity-0 scale-95 translate-y-4"
          >
            <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
              <div class="p-6 border-b border-gray-200">
                <h2 class="text-2xl font-bold text-gray-900">
                  {{ editingProduct ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới' }}
                </h2>
              </div>
              <form @submit.prevent="saveProduct" class="p-6 space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tên sản phẩm *</label>
                  <input
                    v-model="productForm.name"
                    type="text"
                    required
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                    placeholder="VD: Arduino Uno R3"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả *</label>
                  <textarea
                    v-model="productForm.description"
                    required
                    rows="3"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                    placeholder="Mô tả chi tiết về sản phẩm..."
                  ></textarea>
                </div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Giá (VNĐ) *</label>
                    <input
                      v-model.number="productForm.price"
                      type="number"
                      required
                      min="0"
                      step="1000"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                      placeholder="150000"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Tồn kho *</label>
                    <input
                      v-model.number="productForm.stock"
                      type="number"
                      required
                      min="0"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                      placeholder="100"
                    />
                  </div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Danh mục *</label>
                  <select
                    v-model="productForm.category"
                    required
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                  >
                    <option value="">-- Chọn danh mục --</option>
                    <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                      {{ cat.name }}
                    </option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">URL hình ảnh</label>
                  <input
                    v-model="productForm.imageUrl"
                    type="url"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                    placeholder="https://example.com/image.jpg"
                  />
                </div>
                <div class="flex gap-3 pt-4">
                  <button
                    type="submit"
                    class="flex-1 px-4 py-2 bg-[#09f] text-white rounded-lg hover:bg-[#0077cc] transition-colors font-medium"
                  >
                    {{ editingProduct ? 'Cập nhật' : 'Thêm mới' }}
                  </button>
                  <button
                    type="button"
                    @click="closeProductModal"
                    class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                  >
                    Hủy
                  </button>
                </div>
              </form>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>

    <!-- Category Modal -->
    <Teleport to="body">
      <Transition
        enter-active-class="transition-opacity duration-300 ease-out"
        leave-active-class="transition-opacity duration-200 ease-in"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div
          v-if="showCategoryModal"
          class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4"
          @click.self="closeCategoryModal"
        >
          <Transition
            enter-active-class="transition-all duration-300 ease-out"
            leave-active-class="transition-all duration-200 ease-in"
            enter-from-class="opacity-0 scale-95 translate-y-4"
            enter-to-class="opacity-100 scale-100 translate-y-0"
            leave-from-class="opacity-100 scale-100 translate-y-0"
            leave-to-class="opacity-0 scale-95 translate-y-4"
          >
            <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
              <div class="p-6 border-b border-gray-200">
                <h2 class="text-2xl font-bold text-gray-900">
                  {{ editingCategory ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới' }}
                </h2>
              </div>
              <form @submit.prevent="saveCategory" class="p-6 space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Tên danh mục *</label>
                  <input
                    v-model="categoryForm.name"
                    type="text"
                    required
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                    placeholder="VD: Vi điều khiển"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả *</label>
                  <textarea
                    v-model="categoryForm.description"
                    required
                    rows="3"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                    placeholder="Mô tả về danh mục..."
                  ></textarea>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">URL hình ảnh</label>
                  <input
                    v-model="categoryForm.imageUrl"
                    type="url"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#09f] focus:border-transparent"
                    placeholder="https://example.com/image.jpg"
                  />
                </div>
                <div class="flex gap-3 pt-4">
                  <button
                    type="submit"
                    class="flex-1 px-4 py-2 bg-[#09f] text-white rounded-lg hover:bg-[#0077cc] transition-colors font-medium"
                  >
                    {{ editingCategory ? 'Cập nhật' : 'Thêm mới' }}
                  </button>
                  <button
                    type="button"
                    @click="closeCategoryModal"
                    class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                  >
                    Hủy
                  </button>
                </div>
              </form>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuth } from '~/composables/useAuth'
import { useNotification } from '~/composables/useNotification'
import type { Product, Category } from '~/types'

definePageMeta({
  middleware: 'auth'
})

const { user } = useAuth()
const { success, error } = useNotification()

// Check if user is admin
onMounted(() => {
  if (user.value?.role !== 'ROLE_ADMIN') {
    error('Truy cập bị từ chối', 'Bạn không có quyền truy cập trang này')
    navigateTo('/')
  }
})

// State
const activeTab = ref<'products' | 'categories'>('products')
const productSearch = ref('')
const categorySearch = ref('')

// Products
const products = ref<Product[]>([])
const showProductModal = ref(false)
const editingProduct = ref<Product | null>(null)
const productForm = ref({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  category: '',
  imageUrl: '',
  specifications: {}
})

// Categories
const categories = ref<Category[]>([])
const showCategoryModal = ref(false)
const editingCategory = ref<Category | null>(null)
const categoryForm = ref({
  name: '',
  description: '',
  imageUrl: ''
})

// Computed
const filteredProducts = computed(() => {
  if (!productSearch.value) return products.value
  const search = productSearch.value.toLowerCase()
  return products.value.filter(p => 
    p.name.toLowerCase().includes(search) || 
    p.description.toLowerCase().includes(search)
  )
})

const filteredCategories = computed(() => {
  if (!categorySearch.value) return categories.value
  const search = categorySearch.value.toLowerCase()
  return categories.value.filter(c => 
    c.name.toLowerCase().includes(search) || 
    c.description.toLowerCase().includes(search)
  )
})

// Methods
const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const getCategoryName = (categoryId: string) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category?.name || 'N/A'
}

const getProductCountByCategory = (categoryId: string) => {
  return products.value.filter(p => p.category === categoryId).length
}

// Product CRUD
const openProductModal = (product?: Product) => {
  if (product) {
    editingProduct.value = product
    productForm.value = {
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      category: product.category,
      imageUrl: product.imageUrl,
      specifications: product.specifications
    }
  } else {
    editingProduct.value = null
    productForm.value = {
      name: '',
      description: '',
      price: 0,
      stock: 0,
      category: '',
      imageUrl: '',
      specifications: {}
    }
  }
  showProductModal.value = true
}

const closeProductModal = () => {
  showProductModal.value = false
  editingProduct.value = null
}

const saveProduct = async () => {
  try {
    if (editingProduct.value) {
      // Update product
      const index = products.value.findIndex(p => p.id === editingProduct.value!.id)
      if (index !== -1) {
        products.value[index] = {
          ...products.value[index],
          ...productForm.value,
          updatedAt: new Date()
        }
        success('Thành công', 'Đã cập nhật sản phẩm')
      }
    } else {
      // Create product
      const newProduct: Product = {
        id: Date.now().toString(),
        ...productForm.value,
        createdAt: new Date(),
        updatedAt: new Date()
      }
      products.value.push(newProduct)
      success('Thành công', 'Đã thêm sản phẩm mới')
    }
    closeProductModal()
  } catch (e) {
    error('Lỗi', 'Không thể lưu sản phẩm')
  }
}

const deleteProduct = async (id: string) => {
  if (confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
    try {
      products.value = products.value.filter(p => p.id !== id)
      success('Thành công', 'Đã xóa sản phẩm')
    } catch (e) {
      error('Lỗi', 'Không thể xóa sản phẩm')
    }
  }
}

// Category CRUD
const openCategoryModal = (category?: Category) => {
  if (category) {
    editingCategory.value = category
    categoryForm.value = {
      name: category.name,
      description: category.description,
      imageUrl: category.imageUrl || ''
    }
  } else {
    editingCategory.value = null
    categoryForm.value = {
      name: '',
      description: '',
      imageUrl: ''
    }
  }
  showCategoryModal.value = true
}

const closeCategoryModal = () => {
  showCategoryModal.value = false
  editingCategory.value = null
}

const saveCategory = async () => {
  try {
    if (editingCategory.value) {
      // Update category
      const index = categories.value.findIndex(c => c.id === editingCategory.value!.id)
      if (index !== -1) {
        categories.value[index] = {
          ...categories.value[index],
          ...categoryForm.value
        }
        success('Thành công', 'Đã cập nhật danh mục')
      }
    } else {
      // Create category
      const newCategory: Category = {
        id: Date.now().toString(),
        ...categoryForm.value
      }
      categories.value.push(newCategory)
      success('Thành công', 'Đã thêm danh mục mới')
    }
    closeCategoryModal()
  } catch (e) {
    error('Lỗi', 'Không thể lưu danh mục')
  }
}

const deleteCategory = async (id: string) => {
  const hasProducts = products.value.some(p => p.category === id)
  if (hasProducts) {
    error('Không thể xóa', 'Danh mục này đang có sản phẩm')
    return
  }
  
  if (confirm('Bạn có chắc muốn xóa danh mục này?')) {
    try {
      categories.value = categories.value.filter(c => c.id !== id)
      success('Thành công', 'Đã xóa danh mục')
    } catch (e) {
      error('Lỗi', 'Không thể xóa danh mục')
    }
  }
}

// Load initial data (mock data for now)
onMounted(() => {
  // Mock categories
  categories.value = [
    {
      id: '1',
      name: 'Vi điều khiển',
      description: 'Arduino, ESP32, STM32, PIC và các vi điều khiển khác',
      imageUrl: 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=400'
    },
    {
      id: '2',
      name: 'Cảm biến',
      description: 'Cảm biến nhiệt độ, độ ẩm, ánh sáng, chuyển động',
      imageUrl: 'https://images.unsplash.com/photo-1573164713714-d95e436ab8d6?w=400'
    },
    {
      id: '3',
      name: 'IC số',
      description: 'IC 74LS, 74HC, CD4000 series',
      imageUrl: 'https://images.unsplash.com/photo-1597733336794-12d05021d510?w=400'
    }
  ]

  // Mock products
  products.value = [
    {
      id: '1',
      name: 'Arduino Uno R3',
      description: 'Board Arduino Uno R3 chính hãng với chip ATmega328P',
      price: 150000,
      stock: 50,
      category: '1',
      imageUrl: 'https://images.unsplash.com/photo-1553406830-ef2513450d76?w=400',
      specifications: { chip: 'ATmega328P', voltage: '5V' },
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: '2',
      name: 'ESP32 DevKit V1',
      description: 'Module ESP32 có WiFi và Bluetooth tích hợp',
      price: 85000,
      stock: 100,
      category: '1',
      imageUrl: 'https://images.unsplash.com/photo-1517077304055-6e89abbf09b0?w=400',
      specifications: { wifi: 'Yes', bluetooth: 'Yes' },
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: '3',
      name: 'DHT22',
      description: 'Cảm biến nhiệt độ và độ ẩm DHT22',
      price: 65000,
      stock: 75,
      category: '2',
      imageUrl: 'https://images.unsplash.com/photo-1558346490-a72e53ae2d4f?w=400',
      specifications: { accuracy: '±0.5°C', range: '-40~80°C' },
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ]
})
</script>
