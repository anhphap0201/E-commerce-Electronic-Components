<template>
  <div class="bg-gray-50 min-h-screen py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Page Title -->
      <h1 class="text-3xl font-bold text-gray-900 mb-8">Thanh toán</h1>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Left Column: Forms -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Shipping Address -->
          <div class="bg-white rounded-2xl p-6 shadow-sm">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-bold text-gray-900">Địa chỉ giao hàng</h2>
              <span class="text-sm text-[#09f] font-medium">Bước 1/3</span>
            </div>

            <form class="space-y-4">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Họ và tên <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="shippingInfo.fullName"
                    type="text"
                    required
                    class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)]"
                    placeholder="Nhập họ và tên"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Số điện thoại <span class="text-red-500">*</span>
                  </label>
                  <input
                    v-model="shippingInfo.phone"
                    type="tel"
                    required
                    class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)]"
                    placeholder="Nhập số điện thoại"
                  />
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  Email <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="shippingInfo.email"
                  type="email"
                  required
                  class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)]"
                  placeholder="email@example.com"
                />
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Tỉnh/Thành phố <span class="text-red-500">*</span>
                  </label>
                  <select
                    v-model="shippingInfo.provinceCode"
                    @change="handleProvinceChange"
                    required
                    :disabled="provinceLoading"
                    class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)] disabled:bg-gray-100"
                  >
                    <option value="">{{ provinceLoading ? 'Đang tải...' : 'Chọn tỉnh/thành' }}</option>
                    <option v-for="province in provinces" :key="province.code" :value="province.code">
                      {{ province.name }}
                    </option>
                  </select>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Phường/Xã <span class="text-red-500">*</span>
                  </label>
                  <select
                    v-model="shippingInfo.wardCode"
                    required
                    :disabled="!shippingInfo.provinceCode"
                    class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)] disabled:bg-gray-100"
                  >
                    <option value="">Chọn phường/xã</option>
                    <option v-for="ward in wards" :key="ward.code" :value="ward.code">
                      {{ ward.name }}
                    </option>
                  </select>
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  Địa chỉ cụ thể <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="shippingInfo.address"
                  type="text"
                  required
                  class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)]"
                  placeholder="Số nhà, tên đường..."
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  Ghi chú đơn hàng (tùy chọn)
                </label>
                <textarea
                  v-model="shippingInfo.note"
                  rows="3"
                  class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)] resize-none"
                  placeholder="Ghi chú về đơn hàng, ví dụ: thời gian hay chỉ dẫn địa điểm giao hàng chi tiết hơn..."
                ></textarea>
              </div>
            </form>
          </div>

          <!-- Payment Method -->
          <div class="bg-white rounded-2xl p-6 shadow-sm">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-bold text-gray-900">Phương thức thanh toán</h2>
              <span class="text-sm text-[#09f] font-medium">Bước 2/3</span>
            </div>

            <div class="space-y-3">
              <label
                v-for="method in paymentMethods"
                :key="method.id"
                :class="[
                  'flex items-center gap-4 p-4 border-2 rounded-xl cursor-pointer transition-all duration-300',
                  selectedPayment === method.id
                    ? 'border-[#09f] bg-blue-50'
                    : 'border-gray-200 hover:border-[#09f]'
                ]"
              >
                <input
                  type="radio"
                  :value="method.id"
                  v-model="selectedPayment"
                  class="w-5 h-5 text-[#09f] border-gray-300 focus:ring-[#09f] cursor-pointer"
                />
                <div class="flex-1">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 flex items-center justify-center flex-shrink-0">
                      <img
                        v-if="method.image"
                        :src="method.image"
                        :alt="method.name"
                        class="w-full h-full object-contain"
                      />
                      <span v-else class="text-2xl">{{ method.icon }}</span>
                    </div>
                    <div>
                      <p class="font-semibold text-gray-900">{{ method.name }}</p>
                      <p class="text-sm text-gray-600">{{ method.description }}</p>
                    </div>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <!-- Shipping Method -->
          <div class="bg-white rounded-2xl p-6 shadow-sm">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-bold text-gray-900">Phương thức vận chuyển</h2>
              <span class="text-sm text-[#09f] font-medium">Bước 3/3</span>
            </div>

            <div class="space-y-3">
              <label
                v-for="method in shippingMethods"
                :key="method.id"
                :class="[
                  'flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all duration-300',
                  selectedShipping === method.id
                    ? 'border-[#09f] bg-blue-50'
                    : 'border-gray-200 hover:border-[#09f]'
                ]"
              >
                <div class="flex items-center gap-4">
                  <input
                    type="radio"
                    :value="method.id"
                    v-model="selectedShipping"
                    class="w-5 h-5 text-[#09f] border-gray-300 focus:ring-[#09f] cursor-pointer"
                  />
                  <div>
                    <p class="font-semibold text-gray-900">{{ method.name }}</p>
                    <p class="text-sm text-gray-600">{{ method.description }}</p>
                  </div>
                </div>
                <span class="font-bold text-[#09f]">
                  {{ method.fee > 0 ? formatPrice(method.fee) : 'Miễn phí' }}
                </span>
              </label>
            </div>
          </div>
        </div>

        <!-- Right Column: Order Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white rounded-2xl p-6 shadow-sm sticky top-24 space-y-6">
            <h2 class="text-xl font-bold text-gray-900">Đơn hàng ({{ orderItems.length }} sản phẩm)</h2>

            <!-- Order Items -->
            <div class="space-y-4 max-h-64 overflow-y-auto">
              <div
                v-for="item in orderItems"
                :key="item.id"
                class="flex gap-3 pb-4 border-b border-gray-200 last:border-0 last:pb-0"
              >
                <div class="w-16 h-16 flex-shrink-0 bg-gradient-to-br from-blue-50 to-cyan-50 rounded-lg overflow-hidden flex items-center justify-center p-1">
                  <img
                    :src="item.img"
                    :alt="item.name"
                    class="w-full h-full object-contain"
                  />
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-semibold text-gray-900 line-clamp-2">{{ item.name }}</p>
                  <p class="text-sm font-bold mt-1 flex items-center justify-between">
                    <span class="text-[#09f]">{{ formatPrice(item.salePrice || item.price || 0) }}</span>
                    <span class="text-gray-700">Số lượng: {{ item.quantity }}</span>
                  </p>
                
                </div>
              </div>
            </div>

            <!-- Voucher Code -->
            <div class="pt-4 border-t border-gray-200">
              <div class="space-y-3">
                <label class="block text-sm font-semibold text-gray-900">
                  Mã giảm giá
                </label>
                <!-- Voucher Display -->
                <div v-if="appliedVoucher" class="space-y-2">
                  <div class="bg-green-50 border border-green-200 rounded-xl p-3 flex items-center justify-between">
                    <div class="flex items-center gap-2">
                      <svg class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <div>
                        <p class="text-sm font-semibold text-green-700">{{ appliedVoucher.code }}</p>
                        <p class="text-xs text-green-600">{{ appliedVoucher.description }}</p>
                      </div>
                    </div>
                    <button
                      @click="removeVoucher"
                      class="text-red-500 hover:text-red-700 transition-colors"
                      title="Xóa mã"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>
                <div v-else class="bg-gray-50 border-2 border-dashed border-gray-300 rounded-xl p-3 flex items-center justify-between">
                  <span class="text-sm text-gray-500">Chọn hoặc nhập mã giảm giá</span>
                  <button
                    type="button"
                    @click.prevent="showVoucherModal = true"
                    class="px-4 py-2 bg-[#09f] text-white rounded-xl text-sm font-semibold hover:bg-[#0077cc] transition-all duration-300 hover:scale-105 active:scale-95"
                  >
                    Voucher
                  </button>
                </div>
              </div>
            </div>

            <!-- Price Summary -->
            <div class="space-y-3 pt-4 border-t border-gray-200">
              <div class="flex justify-between text-gray-700">
                <span>Tạm tính:</span>
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

            <!-- Place Order Button -->
            <button
              @click="placeOrder"
              :disabled="!isFormValid"
              :class="[
                'w-full py-4 rounded-xl font-semibold text-lg transition-all duration-300',
                isFormValid
                  ? 'bg-gradient-to-r from-[#09f] to-[#0077cc] text-white hover:shadow-xl hover:scale-105 active:scale-95 cursor-pointer'
                  : 'bg-gray-300 text-gray-500 cursor-not-allowed'
              ]"
            >
              Đặt hàng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Voucher Modal -->
  <ClientOnly>
    <Teleport to="body">
      <Transition
        enter-active-class="transition-all duration-300 ease-out"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition-all duration-200 ease-in"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div
          v-if="showVoucherModal"
          class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
          @click.self="showVoucherModal = false"
          style="position: fixed; top: 0; left: 0; right: 0; bottom: 0;"
        >
          <div 
            class="bg-white rounded-2xl shadow-2xl max-w-md w-md max-h-[80vh] overflow-hidden relative z-[10000]"
            @click.stop
          >
            <!-- Modal Header -->
        <div class="bg-gradient-to-r from-[#09f] to-[#0077cc] px-6 py-4 flex items-center justify-between">
          <h3 class="text-xl font-bold text-white">Chọn mã giảm giá</h3>
          <button
            @click="showVoucherModal = false"
            class="text-white hover:bg-white/20 rounded-lg p-1 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Modal Body -->
        <div class="p-6 space-y-4 overflow-y-auto max-h-[calc(80vh-80px)]">
          <!-- Input Section -->
          <div class="space-y-3">
            <label class="block text-sm font-semibold text-gray-900">
              Nhập mã giảm giá
            </label>
            <div class="flex gap-2">
              <input
                v-model="modalVoucherCode"
                type="text"
                placeholder="Nhập mã..."
                class="flex-1 px-4 py-2.5 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)] uppercase"
                @keyup.enter="applyModalVoucher"
              />
              <button
                @click="applyModalVoucher"
                :disabled="!modalVoucherCode.trim()"
                :class="[
                  'px-6 py-2.5 rounded-xl text-sm font-semibold transition-all duration-300',
                  modalVoucherCode.trim()
                    ? 'bg-[#09f] text-white hover:bg-[#0077cc] hover:scale-105 active:scale-95'
                    : 'bg-gray-200 text-gray-400 cursor-not-allowed'
                ]"
              >
                Áp dụng
              </button>
            </div>
          </div>

          <!-- Available Vouchers List -->
          <div class="space-y-3 max-w-xl">
            <p class="text-sm font-semibold text-gray-900">Mã giảm giá khả dụng</p>
            <div class="space-y-2">
              <button
                v-for="voucher in availableVouchers"
                :key="voucher.code"
                @click="selectModalVoucher(voucher)"
                class="w-full p-4 border-2 border-gray-200 rounded-xl hover:border-[#09f] hover:bg-blue-50 transition-all duration-300 text-left"
              >
                <div class="flex items-start gap-3">
                  <div class="flex-1 text-left">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-sm font-bold text-[#09f]">{{ voucher.code }}</span>
                      <span class="text-xs font-semibold text-white bg-red-500 px-2 py-0.5 rounded">
                        {{ voucher.type === 'percentage' ? `-${voucher.value}%` : `-${formatPrice(voucher.value)}` }}
                      </span>
                    </div>
                    <p class="text-xs text-gray-600 mb-2 line-clamp-2">{{ voucher.description }}</p>
                    <p v-if="voucher.minOrder > 0" class="text-xs text-gray-500">
                      Đơn tối thiểu: {{ formatPrice(voucher.minOrder) }}
                    </p>
                  </div>
                  <svg class="w-5 h-5 text-[#09f] flex-shrink-0 mt-1 ml-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
      </Transition>
    </Teleport>
  </ClientOnly>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'

interface OrderItem {
  id: string | number
  name: string
  img: string
  price?: number
  originalPrice?: number
  salePrice?: number
  quantity: number
}

// Provinces API
const provinces = ref<any[]>([])
const wards = ref<any[]>([])
const provinceLoading = ref(false)

const API_URL = 'https://provinces.open-api.vn/api/v2'

// Lấy danh sách tỉnh/thành phố với phường/xã
const fetchProvinces = async () => {
  try {
    provinceLoading.value = true
    const response = await fetch(`${API_URL}/?depth=2`)
    const data = await response.json()
    provinces.value = data || []
  } catch (error) {
    console.error('Error fetching provinces:', error)
  } finally {
    provinceLoading.value = false
  }
}

// Mock order data - should come from cart
const orderItems = ref<OrderItem[]>([
  {
    id: '1',
    name: 'Arduino Uno R3 - Bo mạch phát triển',
    img: '/images/ArduinoUnoR3.jpg',
    originalPrice: 200000,
    salePrice: 140000,
    quantity: 2
  },
  {
    id: '2',
    name: 'ESP32 DevKit V1 - WiFi Bluetooth Module',
    img: '/images/nodemcu-esp32-01.webp',
    originalPrice: 150000,
    salePrice: 105000,
    quantity: 1
  }
])

const shippingInfo = ref({
  fullName: '',
  phone: '',
  email: '',
  provinceCode: '',
  wardCode: '',
  address: '',
  note: ''
})

const paymentMethods = [
  {
    id: 'cod',
    name: 'Thanh toán khi nhận hàng (COD)',
    description: 'Thanh toán bằng tiền mặt khi nhận hàng',
    icon: '💵',
    image: '/payment method/icons8-dollar-50.png'
  },
  {
    id: 'bank',
    name: 'Chuyển khoản ngân hàng',
    description: 'Chuyển khoản qua tài khoản ngân hàng',
    icon: '🏦',
    image: '/payment method/logo-atm.png'
  },
  {
    id: 'momo',
    name: 'Ví MoMo',
    description: 'Thanh toán qua ví điện tử MoMo',
    icon: '📱',
    image: '/payment method/MOMO-Logo-App.png'
  },
  {
    id: 'card',
    name: 'Thẻ tín dụng/Ghi nợ',
    description: 'Visa, Mastercard, JCB',
    icon: '💳',
    image: '/payment method/logo-visa-master-jcb.png'
  }
]

const shippingMethods = [
  {
    id: 'standard',
    name: 'Giao hàng tiêu chuẩn',
    description: 'Giao hàng trong 3-5 ngày',
    fee: 30000
  },
  {
    id: 'fast',
    name: 'Giao hàng nhanh',
    description: 'Giao hàng trong 1-2 ngày',
    fee: 50000
  },
  {
    id: 'express',
    name: 'Giao hàng hỏa tốc',
    description: 'Giao hàng trong 24 giờ',
    fee: 80000
  }
]

const selectedPayment = ref('cod')
const selectedShipping = ref('standard')
const voucherCode = ref('')
const appliedVoucher = ref<any>(null)
const showVoucherModal = ref(false)
const modalVoucherCode = ref('')

// Available vouchers
const availableVouchers = [
  {
    code: 'WELCOME10',
    description: 'Giảm 10% cho đơn hàng đầu tiên',
    type: 'percentage',
    value: 10,
    minOrder: 0
  },
  {
    code: 'FREESHIP',
    description: 'Miễn phí vận chuyển',
    type: 'shipping',
    value: 30000,
    minOrder: 0
  },
  {
    code: 'SAVE50K',
    description: 'Giảm 50.000đ cho đơn từ 500.000đ',
    type: 'fixed',
    value: 50000,
    minOrder: 500000
  }
]

// Computed values
const subtotal = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    const price = item.salePrice || item.price || 0
    return sum + (price * item.quantity)
  }, 0)
})

const discount = computed(() => {
  let productDiscount = orderItems.value.reduce((sum, item) => {
    if (item.originalPrice && item.salePrice) {
      return sum + ((item.originalPrice - item.salePrice) * item.quantity)
    }
    return sum
  }, 0)
  return productDiscount + voucherDiscount.value
})

const voucherDiscount = computed(() => {
  if (!appliedVoucher.value) return 0
  
  const voucher = appliedVoucher.value
  
  if (voucher.type === 'percentage') {
    return Math.floor((subtotal.value * voucher.value) / 100)
  } else if (voucher.type === 'fixed') {
    return voucher.value
  } else if (voucher.type === 'shipping') {
    return 0 // Handled in shippingFee
  }
  
  return 0
})

const shippingFee = computed(() => {
  // Free shipping voucher
  if (appliedVoucher.value?.type === 'shipping') {
    return 0
  }
  
  const method = shippingMethods.find(m => m.id === selectedShipping.value)
  // Free shipping if subtotal >= 500k
  return subtotal.value >= 500000 ? 0 : (method?.fee || 0)
})

const total = computed(() => {
  return subtotal.value + shippingFee.value - voucherDiscount.value
})

const isFormValid = computed(() => {
  return (
    shippingInfo.value.fullName.trim() !== '' &&
    shippingInfo.value.phone.trim() !== '' &&
    shippingInfo.value.email.trim() !== '' &&
    shippingInfo.value.provinceCode !== '' &&
    shippingInfo.value.wardCode !== '' &&
    shippingInfo.value.address.trim() !== '' &&
    selectedPayment.value !== '' &&
    selectedShipping.value !== ''
  )
})

// Methods
const handleProvinceChange = () => {
  shippingInfo.value.wardCode = ''
  if (shippingInfo.value.provinceCode) {
    const selectedProvince = provinces.value.find(p => p.code === shippingInfo.value.provinceCode)
    wards.value = selectedProvince?.wards || []
  } else {
    wards.value = []
  }
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const applyVoucher = () => {
  const code = voucherCode.value.trim().toUpperCase()
  const voucher = availableVouchers.find(v => v.code === code)
  
  if (!voucher) {
    alert('Mã giảm giá không hợp lệ!')
    return
  }
  
  if (voucher.minOrder > subtotal.value) {
    alert(`Đơn hàng phải từ ${formatPrice(voucher.minOrder)} để sử dụng mã này!`)
    return
  }
  
  appliedVoucher.value = voucher
  voucherCode.value = voucher.code
}

const selectVoucher = (voucher: any) => {
  if (voucher.minOrder > subtotal.value) {
    alert(`Đơn hàng phải từ ${formatPrice(voucher.minOrder)} để sử dụng mã này!`)
    return
  }
  
  voucherCode.value = voucher.code
  appliedVoucher.value = voucher
}

const removeVoucher = () => {
  appliedVoucher.value = null
  voucherCode.value = ''
}

const applyModalVoucher = () => {
  const code = modalVoucherCode.value.trim().toUpperCase()
  const voucher = availableVouchers.find(v => v.code === code)
  
  if (!voucher) {
    alert('Mã giảm giá không hợp lệ!')
    return
  }
  
  if (voucher.minOrder > subtotal.value) {
    alert(`Đơn hàng phải từ ${formatPrice(voucher.minOrder)} để sử dụng mã này!`)
    return
  }
  
  appliedVoucher.value = voucher
  voucherCode.value = voucher.code
  modalVoucherCode.value = ''
  showVoucherModal.value = false
}

const selectModalVoucher = (voucher: any) => {
  if (voucher.minOrder > subtotal.value) {
    alert(`Đơn hàng phải từ ${formatPrice(voucher.minOrder)} để sử dụng mã này!`)
    return
  }
  
  appliedVoucher.value = voucher
  voucherCode.value = voucher.code
  modalVoucherCode.value = ''
  showVoucherModal.value = false
}

const placeOrder = () => {
  if (!isFormValid.value) {
    alert('Vui lòng điền đầy đủ thông tin!')
    return
  }

  const orderData = {
    items: orderItems.value,
    shipping: shippingInfo.value,
    paymentMethod: selectedPayment.value,
    shippingMethod: selectedShipping.value,
    total: total.value
  }

  console.log('Order placed:', orderData)
  alert('Đặt hàng thành công! Cảm ơn bạn đã mua hàng.')
  
  // Redirect to order confirmation or home page
  navigateTo('/')
}

// Lifecycle
onMounted(() => {
  fetchProvinces()
})
</script>
