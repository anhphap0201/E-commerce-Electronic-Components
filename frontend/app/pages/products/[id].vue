<template>
  <div class="bg-gray-50 min-h-screen py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Breadcrumb -->
      <nav class="flex items-center gap-2 text-sm text-gray-600 mb-6">
        <NuxtLink to="/" class="hover:text-[#09f] transition-colors">Trang chủ</NuxtLink>
        <span>›</span>
        <NuxtLink to="/search" class="hover:text-[#09f] transition-colors">Sản phẩm</NuxtLink>
        <span>›</span>
        <span class="text-gray-900 font-medium">{{ product?.name }}</span>
      </nav>

      <!-- Product Details -->
      <div v-if="product" class="bg-white rounded-2xl shadow-lg overflow-hidden">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 p-8">
          <!-- Product Images -->
          <div class="space-y-4">
            <!-- Main Image -->
            <div class="aspect-video bg-gradient-to-br from-blue-50 to-cyan-50 rounded-2xl overflow-hidden flex items-center justify-center p-6">
              <img 
                :src="selectedImage" 
                :alt="product.name"
                class="w-full h-full object-contain hover:scale-110 transition-transform duration-300"
              />
            </div>
            
            <!-- Thumbnail Images -->
            <div class="grid grid-cols-4 gap-4">
              <div
                v-for="(image, index) in product.images"
                :key="index"
                @click="selectedImage = image"
                :class="[
                  'aspect-square bg-gray-100 rounded-lg overflow-hidden cursor-pointer border-2 transition-all duration-200',
                  selectedImage === image ? 'border-[#09f] scale-105' : 'border-transparent hover:border-gray-300'
                ]"
              >
                <img 
                  :src="image" 
                  :alt="`${product.name} - ${index + 1}`"
                  class="w-full h-full object-contain p-2"
                />
              </div>
            </div>
          </div>

          <!-- Product Info -->
          <div class="space-y-6">
            <!-- Title & Rating -->
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-3">{{ product.name }}</h1>
              <div class="flex items-center gap-4">
                <div class="flex items-center gap-1">
                  <span v-for="i in 5" :key="i" class="text-yellow-400 text-lg">
                    {{ i <= product.rating ? '★' : '☆' }}
                  </span>
                  <span class="text-sm text-gray-600 ml-2">({{ product.reviewCount }} đánh giá)</span>
                </div>
                <span class="text-sm text-gray-600">|</span>
                <span class="text-sm text-gray-600">Đã bán: {{ product.sold }}</span>
              </div>
            </div>

            <!-- Price -->
            <div class="bg-gray-50 rounded-xl p-6">
              <div class="flex items-baseline gap-4 mb-2">
                <span class="text-4xl font-bold text-[#09f]">{{ formatPrice(product.salePrice) }}</span>
                <span v-if="product.originalPrice > product.salePrice" class="text-xl text-gray-400 line-through">
                  {{ formatPrice(product.originalPrice) }}
                </span>
                <span v-if="product.discount" class="bg-red-500 text-white px-3 py-1 rounded-lg text-sm font-bold">
                  -{{ product.discount }}%
                </span>
              </div>
              <p class="text-sm text-gray-600">Giá đã bao gồm VAT</p>
            </div>

            <!-- Stock Status -->
            <div class="flex items-center gap-2">
              <span class="text-gray-700 font-medium">Tình trạng:</span>
              <span :class="product.stock > 0 ? 'text-green-600' : 'text-red-600'" class="font-semibold">
                {{ product.stock > 0 ? `Còn hàng (${product.stock} sản phẩm)` : 'Hết hàng' }}
              </span>
            </div>

            <!-- Quantity Selector -->
            <div v-if="product.stock > 0">
              <label class="text-gray-700 font-medium block mb-2">Số lượng:</label>
              <div class="flex items-center gap-4">
                <div class="flex items-center border-2 border-gray-300 rounded-xl overflow-hidden">
                  <button 
                    @click="decreaseQuantity"
                    class="w-12 h-12 flex items-center justify-center bg-gray-100 hover:bg-gray-200 transition-colors"
                    :disabled="quantity <= 1"
                  >
                    <span class="text-xl">−</span>
                  </button>
                  <input 
                    v-model.number="quantity" 
                    type="number" 
                    min="1" 
                    :max="product.stock"
                    class="w-16 h-12 text-center font-semibold focus:outline-none [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none" 
                  />
                  <button   
                    @click="increaseQuantity"
                    class="w-12 h-12 flex items-center justify-center bg-gray-100 hover:bg-gray-200 transition-colors"
                    :disabled="quantity >= product.stock"
                  >
                    <span class="text-xl">+</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="flex gap-4">
              <button 
                v-if="product.stock > 0"
                @click="addToCart"
                class="flex-1  text-black py-4 rounded-xl font-semibold text-lg transition-all duration-300 hover:bg-gray-200 hover:shadow-xl hover:scale-105 active:scale-95 bg-gray-100"

              >
                
                Thêm vào giỏ hàng
              </button>
              <button 
                v-if="product.stock > 0"
                @click="buyNow"
                class="flex-1 bg-gradient-to-r from-[#09f] to-[#0077cc] text-white py-4 rounded-xl font-semibold text-lg transition-all duration-300 hover:shadow-xl hover:scale-105 active:scale-95 flex items-center justify-center gap-2"

              >
                Mua ngay
              </button>
              <button 
                v-if="product.stock > 0"
                @click="toggleWishlist"
                :class="[
                  'w-14 h-14 rounded-xl transition-all duration-300 hover:scale-105 active:scale-95 border-2 flex items-center justify-center',
                  isInWishlist ? 'bg-red-50 border-red-500' : 'bg-gray-100 border-gray-300 hover:border-red-500'
                ]"
              >
                <svg 
                  :class="[
                    'w-6 h-6 transition-colors duration-300',
                    isInWishlist ? 'fill-red-500' : 'fill-none stroke-gray-400 hover:stroke-red-500'
                  ]"
                  viewBox="0 0 24 24" 
                  stroke-width="2" 
                  stroke-linecap="round" 
                  stroke-linejoin="round"
                >
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
              </button>
              <button 
                v-else
                disabled
                class="flex-1 bg-gray-300 text-gray-600 py-4 rounded-xl font-semibold text-lg cursor-not-allowed"
              >
                Hết hàng
              </button>
            </div>

            <!-- Warranty & Policies -->
            <div class="border-t border-gray-200 pt-6 space-y-3">
              <div class="flex items-start gap-3">
                
                <div>
                  <p class="font-semibold text-gray-900">Bảo hành chính hãng</p>
                  <p class="text-sm text-gray-600">{{ product.warranty }} tháng</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                
                <div>
                  <p class="font-semibold text-gray-900">Giao hàng nhanh chóng</p>
                  <p class="text-sm text-gray-600">Giao hàng toàn quốc trong 2-3 ngày</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                
                <div>
                  <p class="font-semibold text-gray-900">Đổi trả dễ dàng</p>
                  <p class="text-sm text-gray-600">Đổi trả trong vòng 7 ngày nếu có lỗi</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Product Details Tabs -->
        <div class="border-t border-gray-200">
          <div class="flex border-b border-gray-200">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              :class="[
                'px-8 py-4 font-semibold transition-all duration-200 border-b-2',
                activeTab === tab.id 
                  ? 'border-[#09f] text-[#09f]' 
                  : 'border-transparent text-gray-600 hover:text-[#09f]'
              ]"
            >
              {{ tab.label }}
            </button>
          </div>

          <div class="p-8">
            <!-- Description Tab -->
            <div v-if="activeTab === 'description'" class="prose max-w-none">
              <h3 class="text-xl font-bold text-gray-900 mb-4">Mô tả sản phẩm</h3>
              <p class="text-gray-700 leading-relaxed whitespace-pre-line">{{ product.description }}</p>
            </div>

            <!-- Specifications Tab -->
            <div v-if="activeTab === 'specifications'" class="space-y-4">
              <h3 class="text-xl font-bold text-gray-900 mb-4">Thông số kỹ thuật</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div
                  v-for="(spec, index) in product.specifications"
                  :key="index"
                  class="bg-gray-50 rounded-lg p-4 flex justify-between"
                >
                  <span class="font-medium text-gray-700">{{ spec.label }}:</span>
                  <span class="text-gray-900">{{ spec.value }}</span>
                </div>
              </div>
            </div>

            <!-- Reviews Tab -->
            <div v-if="activeTab === 'reviews'" class="space-y-6">
              <div class="flex items-center justify-between mb-6">
                <h3 class="text-xl font-bold text-gray-900">Đánh giá sản phẩm</h3>
                <button class="bg-[#09f] text-white px-6 py-2 rounded-lg font-medium hover:bg-[#0077cc] transition-colors">
                  Viết đánh giá
                </button>
              </div>

              <!-- Rating Summary -->
              <div class="bg-gradient-to-br from-blue-50 to-cyan-50 rounded-xl p-6 flex items-center gap-8">
                <div class="text-center">
                  <div class="text-5xl font-bold text-[#09f]">{{ product.rating }}<span class="text-3xl">/5</span></div>
                  <div class="flex items-center justify-center gap-1 mt-2">
                    <span v-for="i in 5" :key="i" class="text-yellow-400 text-xl">
                      {{ i <= product.rating ? '★' : '☆' }}
                    </span>
                  </div>
                  <div class="text-sm text-gray-600 mt-1">{{ product.reviewCount }} đánh giá</div>
                </div>
                <div class="flex-1 space-y-2">
                  <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="flex items-center gap-3">
                    <span class="text-sm text-gray-600 w-8">{{ star }}⭐</span>
                    <div class="flex-1 bg-gray-200 rounded-full h-2">
                      <div 
                        :style="{ width: `${(product.ratingDistribution?.[star] || 0)}%` }"
                        class="bg-yellow-400 h-2 rounded-full transition-all duration-500"
                      ></div>
                    </div>
                    <span class="text-sm text-gray-600 w-12">{{ product.ratingDistribution?.[star] || 0 }}%</span>
                  </div>
                </div>
              </div>

              <!-- Individual Reviews -->
              <div class="space-y-4">
                <div
                  v-for="review in product.reviews"
                  :key="review.id"
                  class="border border-gray-200 rounded-xl p-6 hover:shadow-md transition-shadow"
                >
                  <div class="flex items-start justify-between mb-3">
                    <div class="flex items-center gap-3">
                      <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-full flex items-center justify-center text-white font-bold">
                        {{ review.userName.charAt(0).toUpperCase() }}
                      </div>
                      <div>
                        <p class="font-semibold text-gray-900">{{ review.userName }}</p>
                        <div class="flex items-center gap-2">
                          <div class="flex items-center">
                            <span v-for="i in 5" :key="i" class="text-yellow-400">
                              {{ i <= review.rating ? '★' : '☆' }}
                            </span>
                          </div>
                          <span class="text-xs text-gray-500">{{ formatDate(review.date) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <p class="text-gray-700 leading-relaxed">{{ review.comment }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Related Products -->
      <div v-if="relatedProducts.length > 0" class="mt-12">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Sản phẩm liên quan</h2>
        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          <NuxtLink
            v-for="relatedProduct in relatedProducts"
            :key="relatedProduct.id"
            :to="`/products/${relatedProduct.id}`"
            class="bg-white border-2 border-gray-100 rounded-2xl overflow-hidden transition-all duration-300 hover:shadow-xl hover:scale-105 hover:-translate-y-1 hover:border-[#09f] group"
          >
            <div class="aspect-square bg-gradient-to-br from-blue-50 to-cyan-50 flex items-center justify-center p-4">
              <img 
                :src="relatedProduct.img" 
                :alt="relatedProduct.name"
                class="w-full h-full object-contain group-hover:scale-110 transition-transform duration-300"
              />
            </div>
            <div class="p-4">
              <h3 class="text-sm font-semibold text-gray-800 mb-2 line-clamp-2 group-hover:text-[#09f] transition-colors">
                {{ relatedProduct.name }}
              </h3>
              <div class="flex items-center gap-2">
                <span class="text-lg font-bold text-[#09f]">{{ formatPrice(relatedProduct.salePrice) }}</span>
                <span v-if="relatedProduct.originalPrice > relatedProduct.salePrice" class="text-xs text-gray-400 line-through">
                  {{ formatPrice(relatedProduct.originalPrice) }}
                </span>
              </div>
            </div>
          </NuxtLink>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="!product" class="flex items-center justify-center py-20">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-[#09f] border-t-transparent mb-4"></div>
          <p class="text-gray-600">Đang tải thông tin sản phẩm...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const productId = computed(() => route.params.id as string)

// Product data (this should come from API in real application)
const product = ref<any>(null)
const selectedImage = ref('')
const quantity = ref(1)
const activeTab = ref('description')
const isInWishlist = ref(false)

const tabs = [
  { id: 'description', label: 'Mô tả' },
  { id: 'specifications', label: 'Thông số kỹ thuật' },
  { id: 'reviews', label: 'Đánh giá' }
]

// Mock data - replace with API call
const mockProducts = [
  {
    id: '1',
    name: 'Arduino Uno R3 - Bo mạch phát triển',
    description: `Arduino Uno R3 là bo mạch vi điều khiển phổ biến nhất trong dòng sản phẩm Arduino. 
    
Với chip xử lý ATmega328P, bo mạch có 14 chân digital I/O (trong đó 6 chân có thể xuất PWM), 6 chân analog input, thạch anh 16MHz, cổng USB, jack nguồn, header ICSP và nút reset.

Bo mạch hoàn hảo cho người mới bắt đầu và các dự án IoT, automation, robot. Hỗ trợ lập trình với Arduino IDE thông qua cổng USB, rất dễ sử dụng với thư viện phong phú.

Đây là phiên bản chính hãng từ Arduino Italy với chất lượng cao, PCB màu xanh đặc trưng và logo Arduino chính thức.`,
    images: [
      '/images/ArduinoUnoR3.jpg',
      '/images/Edunera_Logo_512.png',
      '/images/Edunera_Logo_512.png',
      '/images/Edunera_Logo_512.png'
    ],
    originalPrice: 200000,
    salePrice: 140000,
    discount: 30,
    stock: 50,
    category: 'Arduino',
    rating: 4.5,
    reviewCount: 128,
    sold: 1542,
    warranty: 12,
    specifications: [
      { label: 'Vi điều khiển', value: 'ATmega328P' },
      { label: 'Điện áp hoạt động', value: '5V' },
      { label: 'Điện áp đầu vào', value: '7-12V' },
      { label: 'Chân Digital I/O', value: '14 (6 PWM)' },
      { label: 'Chân Analog Input', value: '6' },
      { label: 'Dòng DC mỗi chân I/O', value: '20 mA' },
      { label: 'Flash Memory', value: '32 KB' },
      { label: 'SRAM', value: '2 KB' },
      { label: 'EEPROM', value: '1 KB' },
      { label: 'Tốc độ xung nhịp', value: '16 MHz' }
    ],
    ratingDistribution: {
      5: 70,
      4: 20,
      3: 7,
      2: 2,
      1: 1
    },
    reviews: [
      {
        id: 1,
        userName: 'Nguyễn Văn A',
        rating: 5,
        comment: 'Sản phẩm chất lượng, đóng gói cẩn thận. Hoạt động tốt, phù hợp cho người mới học Arduino.',
        date: new Date('2025-12-15'),
        verified: true
      },
      {
        id: 2,
        userName: 'Trần Thị B',
        rating: 4,
        comment: 'Board chạy ổn định, giá cả hợp lý. Giao hàng nhanh.',
        date: new Date('2025-12-10'),
        verified: true
      },
      {
        id: 3,
        userName: 'Lê Văn C',
        rating: 5,
        comment: 'Hàng chính hãng, chất lượng tốt. Shop tư vấn nhiệt tình. Sẽ ủng hộ thêm.',
        date: new Date('2025-12-05'),
        verified: false
      }
    ]
  },
  {
    id: '2',
    name: 'ESP32 DevKit V1 - WiFi Bluetooth Module',
    description: `ESP32 DevKit V1 là bo mạch phát triển dựa trên chip ESP32-WROOM-32 của Espressif. 

Bo mạch tích hợp WiFi 802.11 b/g/n và Bluetooth 4.2 BR/EDR và BLE, lõi kép Tensilica LX6 chạy lên đến 240MHz.

ESP32 có 36 GPIO pins, hỗ trợ giao tiếp I2C, SPI, UART, CAN và nhiều giao thức khác. Thích hợp cho các dự án IoT cần kết nối không dây.

Hỗ trợ lập trình với Arduino IDE, ESP-IDF, MicroPython. Tiêu thụ điện năng thấp với các chế độ deep sleep.`,
    images: [
      '/images/nodemcu-esp32-01.webp',
      '/images/Edunera_Logo_512.png',
      '/images/Edunera_Logo_512.png',
      '/images/Edunera_Logo_512.png'
    ],
    originalPrice: 150000,
    salePrice: 105000,
    discount: 30,
    stock: 35,
    category: 'ESP32',
    rating: 4.7,
    reviewCount: 95,
    sold: 876,
    warranty: 6,
    specifications: [
      { label: 'Chip', value: 'ESP32-WROOM-32' },
      { label: 'CPU', value: 'Dual-core Tensilica LX6' },
      { label: 'Tốc độ xung nhịp', value: 'Up to 240MHz' },
      { label: 'SRAM', value: '520 KB' },
      { label: 'Flash', value: '4 MB' },
      { label: 'WiFi', value: '802.11 b/g/n' },
      { label: 'Bluetooth', value: 'v4.2 BR/EDR và BLE' },
      { label: 'GPIO', value: '36 pins' },
      { label: 'ADC', value: '18 channels, 12-bit' },
      { label: 'Điện áp hoạt động', value: '3.3V' }
    ],
    ratingDistribution: {
      5: 75,
      4: 18,
      3: 5,
      2: 1,
      1: 1
    },
    reviews: [
      {
        id: 1,
        userName: 'Phạm Minh D',
        rating: 5,
        comment: 'ESP32 rất mạnh, WiFi ổn định. Dùng cho dự án smart home rất ok.',
        date: new Date('2025-12-18'),
        verified: true
      },
      {
        id: 2,
        userName: 'Hoàng Thị E',
        rating: 5,
        comment: 'Giá tốt, hàng đẹp. Bluetooth và WiFi hoạt động tốt.',
        date: new Date('2025-12-12'),
        verified: true
      }
    ]
  }
]

const relatedProducts = ref([
  {
    id: '3',
    name: 'Raspberry Pi 4 Model B 4GB RAM',
    img: '/images/raspberry-pi-5-6.jpg',
    originalPrice: 1500000,
    salePrice: 1200000,
    discount: 20
  },
  {
    id: '4',
    name: 'LED RGB 5050 SMD - 100 chiếc',
    img: '/images/images.jpg',
    originalPrice: 100000,
    salePrice: 70000,
    discount: 30
  },
  {
    id: '5',
    name: 'Cảm biến nhiệt độ DHT11',
    img: '/images/cam-bien-vat-can-hong-ngoai-fm52-5.jpg',
    originalPrice: 50000,
    salePrice: 35000,
    discount: 30
  },
  {
    id: '6',
    name: 'Motor DC 6V giảm tốc',
    img: '/images/dong-co-servo-sg90-180-do-rk7a-1.jpg',
    originalPrice: 80000,
    salePrice: 56000,
    discount: 30
  },
  {
    id: '7',
    name: 'Relay 5V 1 kênh',
    img: '/images/Relay.jpg',
    originalPrice: 30000,
    salePrice: 21000,
    discount: 30
  }
])

onMounted(() => {
  // Load product data
  loadProduct()
})

const loadProduct = () => {
  // Simulate API call
  setTimeout(() => {
    const foundProduct = mockProducts.find(p => p.id === productId.value)
    if (foundProduct) {
      product.value = foundProduct
      selectedImage.value = foundProduct.images[0]
    }
  }, 500)
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const formatDate = (date: Date) => {
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(date)
}

const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const addToCart = () => {
  // Implement add to cart logic
  alert(`Đã thêm ${quantity.value} sản phẩm vào giỏ hàng!`)
}

const buyNow = () => {
  // Implement buy now logic
  navigateTo('/checkout')
}

// Watch for route changes to reload product
watch(() => route.params.id, () => {
  if (route.params.id) {
    loadProduct()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
})
</script>
