<template>
  <div class="bg-gray-50 min-h-screen py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <nav class="flex items-center gap-2 text-sm text-gray-600 mb-6">
        <NuxtLink to="/" class="hover:text-[#09f] transition-colors">Trang chủ</NuxtLink>
        <span>›</span>
        <span class="text-gray-900 font-medium">Giỏ hàng</span>
      </nav>

      <!-- Cart Title -->
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-3xl font-bold text-gray-900">Giỏ hàng của bạn</h1>
        <span class="text-sm text-gray-600">{{ cartItems.length }} sản phẩm</span>
      </div>

      <div v-if="cartItems.length > 0" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Cart Items -->
        <div class="lg:col-span-2 space-y-4">
          <!-- Select All -->
          <div class="bg-white rounded-2xl p-4 shadow-sm">
            <Checkbox v-model="selectAll" @update:modelValue="toggleSelectAll">
              <span class="text-sm font-medium text-gray-700">
                Chọn tất cả ({{ cartItems.length }} sản phẩm)
              </span>
            </Checkbox>
          </div>

          <div
            v-for="item in cartItems"
            :key="item.id"
            :class="[
              'bg-white rounded-2xl p-6 shadow-sm transition-all duration-300',
              item.selected ? 'ring-2 ring-[#09f] shadow-md' : 'hover:shadow-md'
            ]"
          >
            <div class="flex gap-4">
              <!-- Checkbox -->
              <div class="flex items-start pt-1">
                <Checkbox v-model="item.selected" />
              </div>
              <!-- Product Image -->
              <NuxtLink 
                :to="`/products/${item.id}`"
                class="w-24 h-24 flex-shrink-0 bg-gradient-to-br from-blue-50 to-cyan-50 rounded-xl overflow-hidden flex items-center justify-center p-2"
              >
                <img 
                  :src="item.img" 
                  :alt="item.name"
                  class="w-full h-full object-contain hover:scale-110 transition-transform duration-300"
                />
              </NuxtLink>

              <!-- Product Info -->
              <div class="flex-1 min-w-0">
                <NuxtLink 
                  :to="`/products/${item.id}`"
                  class="text-lg font-semibold text-gray-900 hover:text-[#09f] transition-colors line-clamp-2"
                >
                  {{ item.name }}
                </NuxtLink>
                
                <p v-if="item.category" class="text-sm text-gray-500 mt-1">{{ item.category }}</p>
                
                <!-- Price -->
                <div class="flex items-baseline gap-2 mt-2">
                  <span class="text-xl font-bold text-[#09f]">
                    {{ formatPrice(item.salePrice || item.price) }}
                  </span>
                  <span v-if="item.originalPrice && item.originalPrice > (item.salePrice || item.price)" class="text-sm text-gray-400 line-through">
                    {{ formatPrice(item.originalPrice) }}
                  </span>
                  <span v-if="item.discount" class="bg-red-500 text-white px-2 py-0.5 rounded text-xs font-bold ml-1">
                    -{{ item.discount }}%
                  </span>
                </div>

                <!-- Quantity Controls & Remove -->
                <div class="flex items-center justify-between mt-4">
                  <div class="flex items-center border-2 border-gray-300 rounded-xl overflow-hidden">
                    <button 
                      @click="decreaseQuantity(item.id)"
                      class="w-10 h-10 flex items-center justify-center bg-gray-100 hover:bg-gray-200 transition-colors"
                      :disabled="item.quantity <= 1"
                    >
                      <span class="text-lg">−</span>
                    </button>
                    <input 
                      v-model.number="item.quantity" 
                      type="number" 
                      min="1" 
                      :max="item.stock || 99"
                      class="w-14 h-10 text-center font-semibold focus:outline-none [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"
                      @change="updateQuantity(item.id, item.quantity)"
                    />
                    <button 
                      @click="increaseQuantity(item.id)"
                      class="w-10 h-10 flex items-center justify-center bg-gray-100 hover:bg-gray-200 transition-colors"
                      :disabled="item.quantity >= (item.stock || 99)"
                    >
                      <span class="text-lg">+</span>
                    </button>
                  </div>

                  <!-- Subtotal & Remove -->
                  <div class="flex items-center gap-4">
                    <span class="text-lg font-bold text-gray-900">
                      {{ formatPrice((item.salePrice || item.price) * item.quantity) }}
                    </span>
                    <button 
                      @click="removeItem(item.id)"
                      class="w-10 h-10 rounded-xl flex items-center justify-center text-gray-400 hover:text-red-500 hover:bg-red-50 transition-all duration-300"
                      title="Xóa sản phẩm"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Continue Shopping -->
          <NuxtLink
            to="/search"
            class="block w-full bg-white text-center py-4 rounded-2xl border-2 border-gray-200 text-gray-700 font-semibold hover:border-[#09f] hover:text-[#09f] hover:bg-blue-50 transition-all duration-300"
          >
            ← Tiếp tục mua sắm
          </NuxtLink>
        </div>

        <!-- Order Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white rounded-2xl p-6 shadow-sm sticky top-24 space-y-6">
            <h2 class="text-xl font-bold text-gray-900">Tổng đơn hàng</h2>

            <!-- Summary Details -->
            <div class="space-y-3">
              <div class="flex justify-between text-gray-700">
                <span>Tạm tính ({{ selectedItemsCount }} sản phẩm đã chọn):</span>
                <span class="font-semibold">{{ formatPrice(subtotal) }}</span>
              </div>
              
              <div class="flex justify-between text-gray-700">
                <span>Giảm giá:</span>
                <span class="font-semibold text-red-500">-{{ formatPrice(discount) }}</span>
              </div>
              
              <div class="flex justify-between text-gray-700">
                <span>Phí vận chuyển:</span>
                <span class="font-semibold">{{ shippingFee > 0 ? formatPrice(shippingFee) : 'Miễn phí' }}</span>
              </div>
              
              <div class="border-t border-gray-200 pt-3">
                <div class="flex justify-between items-baseline">
                  <span class="text-lg font-semibold text-gray-900">Tổng cộng:</span>
                  <span class="text-2xl font-bold text-[#09f]">{{ formatPrice(total) }}</span>
                </div>
              </div>
            </div>

            <!-- Shipping Notice -->
            <div v-if="subtotal < 500000" class="bg-blue-50 border border-blue-200 rounded-xl p-4">
              <p class="text-sm text-blue-700">
                🚚 Mua thêm <span class="font-bold">{{ formatPrice(500000 - subtotal) }}</span> để được miễn phí vận chuyển
              </p>
              <div class="w-full bg-blue-200 rounded-full h-2 mt-2">
                <div 
                  class="bg-blue-500 h-2 rounded-full transition-all duration-500"
                  :style="{ width: `${(subtotal / 500000) * 100}%` }"
                ></div>
              </div>
            </div>

            <!-- Checkout Button -->
            <button 
              @click="goToCheckout"
              :disabled="selectedItemsCount === 0"
              :class="[
                'w-full py-4 rounded-xl font-semibold text-lg transition-all duration-300',
                selectedItemsCount > 0 
                  ? 'bg-gradient-to-r from-[#09f] to-[#0077cc] text-white hover:shadow-xl hover:scale-105 active:scale-95 cursor-pointer'
                  : 'bg-gray-300 text-gray-500 cursor-not-allowed'
              ]"
            >
              Tiến hành thanh toán{{ selectedItemsCount > 0 ? ` (${selectedItemsCount})` : '' }}
            </button>

            <!-- Payment Methods -->
            <div class="border-t border-gray-200 pt-4">
              <p class="text-sm text-gray-600 mb-3">Phương thức thanh toán:</p>
              <div class="flex gap-2">
                <div class="w-12 h-8 bg-gray-100 rounded border border-gray-200 flex items-center justify-center text-xs font-semibold text-gray-600">
                  VISA
                </div>
                <div class="w-12 h-8 bg-gray-100 rounded border border-gray-200 flex items-center justify-center text-xs font-semibold text-gray-600">
                  MC
                </div>
                <div class="w-12 h-8 bg-gray-100 rounded border border-gray-200 flex items-center justify-center text-xs font-semibold text-gray-600">
                  COD
                </div>
                <div class="w-12 h-8 bg-gray-100 rounded border border-gray-200 flex items-center justify-center text-xs font-semibold text-gray-600">
                  ATM
                </div>
              </div>
            </div>

            <!-- Security -->
            <div class="flex items-center gap-2 text-sm text-gray-600">
              <svg class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
              <span>Giao dịch bảo mật và mã hóa</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty Cart -->
      <div v-else class="bg-white rounded-2xl p-12 text-center">
        <div class="w-32 h-32 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
          <svg class="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 mb-2">Giỏ hàng trống</h2>
        <p class="text-gray-600 mb-8">Bạn chưa có sản phẩm nào trong giỏ hàng</p>
        <NuxtLink
          to="/search"
          class="inline-block px-8 py-3 bg-gradient-to-r from-[#09f] to-[#0077cc] text-white rounded-xl font-semibold transition-all duration-300 hover:shadow-xl hover:scale-105"
        >
          Khám phá sản phẩm
        </NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

const selectAll = ref(false)

interface CartItem {
  id: string | number
  name: string
  img: string
  price?: number
  originalPrice?: number
  salePrice?: number
  discount?: number
  quantity: number
  stock?: number
  category?: string
  selected?: boolean
}

// Mock cart data - replace with actual cart state management (Vuex/Pinia)
const cartItems = ref<CartItem[]>([
  {
    id: '1',
    name: 'Arduino Uno R3 - Bo mạch phát triển',
    img: '/images/ArduinoUnoR3.jpg',
    originalPrice: 200000,
    salePrice: 140000,
    discount: 30,
    quantity: 2,
    stock: 50,
    category: 'Arduino',
    selected: true
  },
  {
    id: '2',
    name: 'ESP32 DevKit V1 - WiFi Bluetooth Module',
    img: '/images/nodemcu-esp32-01.webp',
    originalPrice: 150000,
    salePrice: 105000,
    discount: 30,
    quantity: 1,
    stock: 35,
    category: 'ESP32',
    selected: true
  },
  {
    id: '5',
    name: 'Cảm biến nhiệt độ DHT11',
    img: '/images/cam-bien-vat-can-hong-ngoai-fm52-5.jpg',
    originalPrice: 50000,
    salePrice: 35000,
    discount: 30,
    quantity: 3,
    stock: 100,
    category: 'Cảm biến',
    selected: false
  }
])

// Computed values
const selectedItems = computed(() => {
  return cartItems.value.filter(item => item.selected)
})

const selectedItemsCount = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalItems = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
})

// Sync selectAll with isAllSelected
watch(isAllSelected, (newValue) => {
  selectAll.value = newValue
}, { immediate: true })

const subtotal = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    const price = item.salePrice || item.price || 0
    return sum + (price * item.quantity)
  }, 0)
})

const discount = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    if (item.originalPrice && item.salePrice) {
      return sum + ((item.originalPrice - item.salePrice) * item.quantity)
    }
    return sum
  }, 0)
})

const shippingFee = computed(() => {
  return subtotal.value >= 500000 ? 0 : 30000
})

const total = computed(() => {
  return subtotal.value + shippingFee.value
})

// Methods
const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const increaseQuantity = (itemId: string | number) => {
  const item = cartItems.value.find(i => i.id === itemId)
  if (item && item.quantity < (item.stock || 99)) {
    item.quantity++
  }
}

const decreaseQuantity = (itemId: string | number) => {
  const item = cartItems.value.find(i => i.id === itemId)
  if (item && item.quantity > 1) {
    item.quantity--
  }
}

const updateQuantity = (itemId: string | number, quantity: number) => {
  const item = cartItems.value.find(i => i.id === itemId)
  if (item) {
    const newQuantity = Math.max(1, Math.min(quantity, item.stock || 99))
    item.quantity = newQuantity
  }
}

const removeItem = (itemId: string | number) => {
  const index = cartItems.value.findIndex(i => i.id === itemId)
  if (index !== -1) {
    if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
      cartItems.value.splice(index, 1)
    }
  }
}

const toggleSelectAll = () => {
  const newValue = !isAllSelected.value
  cartItems.value.forEach(item => {
    item.selected = newValue
  })
}

const goToCheckout = () => {
  if (selectedItemsCount.value === 0) {
    alert('Vui lòng chọn ít nhất một sản phẩm để thanh toán')
    return
  }
  // Implement checkout logic with selected items only
  const selectedProducts = selectedItems.value
  console.log('Checkout with:', selectedProducts)
  navigateTo('/checkout')
}
</script>
