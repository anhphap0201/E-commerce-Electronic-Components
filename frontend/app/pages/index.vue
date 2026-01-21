<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- Banner Carousel -->
    <section class="bg-white border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div class="relative overflow-hidden rounded-2xl bg-gradient-to-r from-[#09f] to-[#0077cc] h-64">
          <Transition
            enter-active-class="transition-all duration-700 ease-out"
            leave-active-class="transition-all duration-700 ease-in absolute inset-0"
            enter-from-class="opacity-0 translate-x-full"
            enter-to-class="opacity-100 translate-x-0"
            leave-from-class="opacity-100 translate-x-0"
            leave-to-class="opacity-0 -translate-x-full"
          >
            <div :key="currentBanner" class="absolute inset-0 flex items-center justify-center p-8">
              <div class="text-center text-white">
                <h2 class="text-4xl font-bold mb-3">{{ banners[currentBanner]?.title }}</h2>
                <p class="text-xl mb-6 text-blue-100">{{ banners[currentBanner]?.description }}</p>
                <button class="px-8 py-3 bg-white text-[#09f] rounded-xl font-semibold text-lg transition-all duration-300 hover:bg-blue-50 hover:scale-105 hover:shadow-xl">
                  {{ banners[currentBanner]?.action }}
                </button>
              </div>
            </div>
          </Transition>
          
          <!-- Banner Indicators -->
          <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
            <button
              v-for="(_, index) in banners"
              :key="index"
              @click="currentBanner = index"
              :class="[
                'w-2 h-2 rounded-full transition-all duration-300',
                currentBanner === index ? 'w-8 bg-white' : 'bg-white/50 hover:bg-white/80'
              ]"
            ></button>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories -->
    <section class="py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-gray-800">Danh Mục Sản Phẩm</h2>
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-10 gap-4">
          <div
            v-for="category in categories"
            :key="category.id"
            class="bg-white rounded-2xl p-4 flex flex-col items-center justify-center gap-3 transition-all duration-300 hover:shadow-lg hover:scale-105 hover:-translate-y-1 cursor-pointer border-2 border-transparent hover:border-[#09f] group"
            @click="handleSearch(category.name)"
          >
            <div class="bg-none w-16 h-16 bg-gradient-to-br from-blue-50 to-cyan-50 rounded-xl flex items-center justify-center overflow-hidden transition-all duration-300 group-hover:scale-110">
              <img 
                :src="getCategoryImage(category)" 
                :alt="category.name"
                class="w-16 h-16 object-contain "
              />
            </div>
            <span class="text-xs text-center text-gray-700 font-medium group-hover:text-[#09f] transition-colors duration-200">
              {{ category.name }}
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- Sale Products -->
    <section class="py-12 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Sản Phẩm Đang Sale</h2>
            <p class="text-sm text-gray-500 mt-1">Giảm giá lên đến 50%</p>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          <NuxtLink
            v-for="product in saleProducts"
            :key="product.id"
            :to="`/products/${product.id}`"
            class="bg-white border-2 border-gray-100 rounded-2xl overflow-hidden transition-all duration-300 hover:shadow-xl hover:scale-105 hover:-translate-y-1 hover:border-[#09f] group cursor-pointer"
          >
            <!-- Product Image -->
            <div class=" relative aspect-square bg-gradient-to-br from-blue-50 to-cyan-50 flex items-center justify-center overflow-hidden p-6">
              <img 
                :src="product.img" 
                :alt="product.name"
                class="w-full h-full object-contain group-hover:scale-110 transition-transform duration-300"
              />
              <!-- Sale Badge -->
              <div class="absolute top-3 right-3 bg-red-500 text-white px-3 py-1 rounded-lg text-xs font-bold">
                -{{ product.discount }}%
              </div>
            </div>

            <!-- Product Info -->
            <div class="p-4">
              <h3 class="text-sm font-semibold text-gray-800 mb-2 line-clamp-2 group-hover:text-[#09f] transition-colors duration-200">
                {{ product.name }}
              </h3>
              
              <!-- Price -->
              <div class="flex items-center gap-2 mb-3">
                <span class="text-lg font-bold text-[#09f]">
                  {{ formatPrice(product.salePrice) }}
                </span>
                <span class="text-sm text-gray-400 line-through">
                  {{ formatPrice(product.originalPrice) }}
                </span>
              </div>

              <!-- Actions -->
              <div class="flex gap-2">
                <button @click.prevent="buyNow(product.id)" class="flex-1 bg-[#09f] text-white py-2 rounded-xl text-sm font-medium transition-all duration-300 hover:bg-[#0077cc] hover:shadow-lg active:scale-95">
                  Mua ngay
                </button>
                <button @click.prevent="addToCart(product.id)" class="w-10 h-10 border-2 border-gray-200 rounded-xl flex items-center justify-center transition-all duration-300 hover:border-[#09f] hover:text-[#09f] hover:scale-105 active:scale-95">
                  🛒
                </button>
              </div>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const banners = [
  {
    title: 'Giảm Giá 30% Tất Cả Sản Phẩm',
    description: 'Chương trình khuyến mãi lớn - Áp dụng đến hết tháng',
    action: 'Mua Ngay'
  },
  {
    title: 'Arduino Nano Mới Về',
    description: 'Hàng chính hãng - Giá tốt nhất thị trường',
    action: 'Xem Chi Tiết'
  },
  {
    title: 'Miễn Phí Vận Chuyển',
    description: 'Cho đơn hàng từ 500.000đ',
    action: 'Khám Phá'
  }
]

// Fetch categories from API
const categories = ref<any[]>([])

// Default images for categories
const categoryImages: Record<string, string> = {
  'Arduino & Modules': '/images/ArduinoUnoR3.jpg',
  'Raspberry Pi': '/images/raspberry-pi-5-6.jpg',
  'Linh kiện điện tử': '/images/resistores.jpg',
  'Cảm biến': '/images/cam-bien-vat-can-hong-ngoai-fm52-5.jpg',
  'Module nguồn': '/images/61GYgJyVv5L.jpg',
  'Module truyền thông': '/images/nodemcu-esp32-01.webp',
  'Màn hình hiển thị': '/images/man-hinh-lcd-oled-0-96-inch-giao-tiep-i2c-white-9w56-1.jpg',
  'Motor & Driver': '/images/dong-co-servo-sg90-180-do-rk7a-1.jpg',
  'default': '/images/pngtree-ellipsis-black-glyph-ui-icon-flat-negative-space-ellipsis-vector-png-image_48303394.jpg'
}

const getCategoryImage = (category: any) => {
  return category.imageUrl || categoryImages[category.name] || categoryImages['default']
}

const saleProducts = [
  {
    id: 1,
    name: 'Arduino Uno R3 - Bo mạch phát triển',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 200000,
    salePrice: 140000,
    discount: 30
  },
  {
    id: 2,
    name: 'ESP32 DevKit V1 - WiFi Bluetooth',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 150000,
    salePrice: 105000,
    discount: 30
  },
  {
    id: 3,
    name: 'Raspberry Pi 4 Model B 4GB RAM',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 1500000,
    salePrice: 1200000,
    discount: 20
  },
  {
    id: 4,
    name: 'LED RGB 5050 SMD - 100 chiếc',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 100000,
    salePrice: 70000,
    discount: 30
  },
  {
    id: 5,
    name: 'Cảm biến nhiệt độ DHT11',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 50000,
    salePrice: 35000,
    discount: 30
  },
  {
    id: 6,
    name: 'Motor DC 6V giảm tốc',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 80000,
    salePrice: 56000,
    discount: 30
  },
  {
    id: 7,
    name: 'Relay 5V 1 kênh',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 30000,
    salePrice: 21000,
    discount: 30
  },
  {
    id: 8,
    name: 'Màn hình LCD 16x2',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 120000,
    salePrice: 84000,
    discount: 30
  },
  {
    id: 9,
    name: 'Cảm biến siêu âm HC-SR04',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 40000,
    salePrice: 28000,
    discount: 30
  },
  {
    id: 10,
    name: 'Module Bluetooth HC-05',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 90000,
    salePrice: 63000,
    discount: 30
  }
]

const currentBanner = ref(0)
let bannerInterval: ReturnType<typeof setInterval>

onMounted(async () => {
  // Auto slide banner every 8 seconds
  bannerInterval = setInterval(() => {
    currentBanner.value = (currentBanner.value + 1) % banners.length
  }, 8000)

  // Fetch categories from API
  try {
    const data = await $fetch('http://localhost:8080/api/categories')
    categories.value = data as any[]
  } catch (error) {
    console.error('Error fetching categories:', error)
  }
})

onUnmounted(() => {
  if (bannerInterval) {
    clearInterval(bannerInterval)
  }
})

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}
const handleSearch = (categoryName: string) => {
  if (categoryName?.trim()) {
    navigateTo(`/search?q=${encodeURIComponent(categoryName)}`)
  }
}

const buyNow = (productId: number) => {
  // Implement buy now functionality
  alert(`Mua ngay sản phẩm ${productId}`)
}

const addToCart = (productId: number) => {
  // Implement add to cart functionality
  alert(`Đã thêm sản phẩm ${productId} vào giỏ hàng`)
}
</script>
