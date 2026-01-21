<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- Breadcrumb -->
    <section class="bg-white border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3">
        <div class="flex items-center gap-2 text-sm text-gray-600">
          <NuxtLink to="/" class="hover:text-[#09f] transition-colors duration-200">
            Trang chủ
          </NuxtLink>
          <span>/</span>
          <span class="text-gray-900 font-medium">Kết quả tìm kiếm</span>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Search Info -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900 mb-2">
          Kết quả tìm kiếm cho: <span class="text-[#09f]">"{{ route.query.q }}"</span>
        </h1>
        <p class="text-gray-600">
          Tìm thấy <span class="font-semibold text-gray-900">{{ filteredProducts.length }}</span> sản phẩm
        </p>
      </div>

      <div class="flex gap-6">
        <!-- Filters Sidebar -->
        <aside class="w-64 flex-shrink-0 hidden lg:block">
          <div class="bg-white rounded-2xl p-5 sticky top-24 space-y-6">
            <!-- Category Filter -->
            <div>
              <h3 class="font-semibold text-gray-900 mb-3 flex items-center justify-between">
                Danh mục
                <button
                  v-if="selectedCategory"
                  @click="selectedCategory = null"
                  class="text-xs text-[#09f] hover:text-[#0077cc] transition-colors duration-200"
                >
                  Xóa
                </button>
              </h3>
              <div class="space-y-2 max-h-64 overflow-y-auto">
                <label
                  v-for="category in categories"
                  :key="category.id"
                  class="flex items-center gap-2 cursor-pointer group"
                >
                  <input
                    type="radio"
                    :value="category.name"
                    v-model="selectedCategory"
                    class="w-4 h-4 text-[#09f] border-gray-300 focus:ring-[#09f] focus:ring-2 cursor-pointer"
                  />
                  <span class="text-sm text-gray-700 group-hover:text-[#09f] transition-colors duration-200">
                    {{ category.name }}
                  </span>
                </label>
              </div>
            </div>

            <!-- Price Filter -->
            <div>
              <h3 class="font-semibold text-gray-900 mb-3 flex items-center justify-between">
                Khoảng giá
                <button
                  v-if="selectedPriceRange"
                  @click="selectedPriceRange = null"
                  class="text-xs text-[#09f] hover:text-[#0077cc] transition-colors duration-200"
                >
                  Xóa
                </button>
              </h3>
              <div class="space-y-2">
                <label
                  v-for="range in priceRanges"
                  :key="range.id"
                  class="flex items-center gap-2 cursor-pointer group"
                >
                  <input
                    type="radio"
                    :value="range.id"
                    v-model="selectedPriceRange"
                    class="w-4 h-4 text-[#09f] border-gray-300 focus:ring-[#09f] focus:ring-2 cursor-pointer"
                  />
                  <span class="text-sm text-gray-700 group-hover:text-[#09f] transition-colors duration-200">
                    {{ range.label }}
                  </span>
                </label>
              </div>
            </div>

            <!-- Sale Filter -->
            <div>
              <h3 class="font-semibold text-gray-900 mb-3">Khác</h3>
              <label class="flex items-center gap-2 cursor-pointer group">
                <input
                  type="checkbox"
                  v-model="onlyShowSale"
                  class="w-4 h-4 text-[#09f] border-gray-300 rounded focus:ring-[#09f] focus:ring-2 cursor-pointer"
                />
                <span class="text-sm text-gray-700 group-hover:text-[#09f] transition-colors duration-200">
                  Chỉ hiển thị sản phẩm giảm giá
                </span>
              </label>
            </div>

            <!-- Clear All Filters -->
            <button
              v-if="hasActiveFilters"
              @click="clearAllFilters"
              class="w-full px-4 py-2 border-2 border-gray-300 text-gray-700 rounded-xl text-sm font-medium transition-all duration-300 hover:border-[#09f] hover:text-[#09f] hover:bg-blue-50"
            >
              Xóa tất cả bộ lọc
            </button>
          </div>
        </aside>

        <!-- Products Grid -->
        <div class="flex-1">
          <!-- Sort Options -->
          <div class="bg-white rounded-2xl p-4 mb-6 flex items-center justify-between flex-wrap gap-3">
            <div class="flex items-center gap-2 text-sm text-gray-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h6m4 0l4-4m0 0l4 4m-4-4v12" />
              </svg>
              <span>Sắp xếp theo:</span>
            </div>
            <div class="flex gap-2 flex-wrap">
              <button
                v-for="option in sortOptions"
                :key="option.value"
                @click="sortBy = option.value"
                :class="[
                  'px-4 py-2 rounded-xl text-sm font-medium transition-all duration-300',
                  sortBy === option.value
                    ? 'bg-[#09f] text-white shadow-lg'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                ]"
              >
                {{ option.label }}
              </button>
            </div>
          </div>

          <!-- Products List -->
          <div v-if="sortedProducts.length > 0" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
            <NuxtLink
              v-for="product in paginatedProducts"
              :key="product.id"
              :to="`/products/${product.id}`"
              class="bg-white border-2 border-gray-100 rounded-2xl overflow-hidden transition-all duration-300 hover:shadow-xl hover:scale-105 hover:-translate-y-1 hover:border-[#09f] group cursor-pointer"
            >
              <!-- Product Image -->
              <div class="relative aspect-square bg-gradient-to-br from-blue-50 to-cyan-50 flex items-center justify-center overflow-hidden p-6">
                <img 
                  :src="product.img" 
                  :alt="product.name"
                  class="w-full h-full object-contain group-hover:scale-110 transition-transform duration-300"
                />
                <!-- Sale Badge -->
                <div v-if="product.discount" class="absolute top-3 right-3 bg-red-500 text-white px-3 py-1 rounded-lg text-xs font-bold">
                  -{{ product.discount }}%
                </div>
                <!-- Category Badge -->
                <div class="absolute top-3 left-3 bg-white/90 backdrop-blur-sm text-gray-700 px-3 py-1 rounded-lg text-xs font-medium">
                  {{ product.category }}
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
                    {{ formatPrice(product.discount ? product.salePrice : product.originalPrice) }}
                  </span>
                  <span v-if="product.discount" class="text-sm text-gray-400 line-through">
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

          <!-- Empty State -->
          <div v-else class="bg-white rounded-2xl p-12 text-center">
            <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2">Không tìm thấy sản phẩm</h3>
            <p class="text-gray-600 mb-6">Thử điều chỉnh bộ lọc hoặc tìm kiếm từ khóa khác</p>
            <button
              @click="clearAllFilters"
              class="px-6 py-2.5 bg-[#09f] text-white rounded-xl font-medium transition-all duration-300 hover:bg-[#0077cc] hover:shadow-lg hover:scale-105"
            >
              Xóa bộ lọc
            </button>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="mt-8 flex justify-center items-center gap-2">
            <button
              @click="currentPage--"
              :disabled="currentPage === 1"
              class="w-10 h-10 rounded-xl border-2 border-gray-200 flex items-center justify-center transition-all duration-300 hover:border-[#09f] hover:text-[#09f] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:border-gray-200 disabled:hover:text-gray-400"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
            </button>

            <div class="flex gap-2">
              <button
                v-for="page in displayedPages"
                :key="page"
                @click="currentPage = page"
                :class="[
                  'w-10 h-10 rounded-xl font-medium transition-all duration-300',
                  currentPage === page
                    ? 'bg-[#09f] text-white shadow-lg scale-110'
                    : 'border-2 border-gray-200 text-gray-700 hover:border-[#09f] hover:text-[#09f]'
                ]"
              >
                {{ page }}
              </button>
            </div>

            <button
              @click="currentPage++"
              :disabled="currentPage === totalPages"
              class="w-10 h-10 rounded-xl border-2 border-gray-200 flex items-center justify-center transition-all duration-300 hover:border-[#09f] hover:text-[#09f] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:border-gray-200 disabled:hover:text-gray-400"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const searchQuery = ref(String(route.query.q || ''))
const selectedCategory = ref<string | null>(null)
const selectedPriceRange = ref<string | null>(null)
const onlyShowSale = ref(false)
const sortBy = ref('relevant')
const currentPage = ref(1)
const itemsPerPage = 12

// Fetch categories from API
const categories = ref<any[]>([])

const priceRanges = [
  { id: '0-100k', label: 'Dưới 100.000đ', min: 0, max: 100000 },
  { id: '100k-500k', label: '100.000đ - 500.000đ', min: 100000, max: 500000 },
  { id: '500k-1m', label: '500.000đ - 1.000.000đ', min: 500000, max: 1000000 },
  { id: '1m-5m', label: '1.000.000đ - 5.000.000đ', min: 1000000, max: 5000000 },
  { id: '5m+', label: 'Trên 5.000.000đ', min: 5000000, max: Infinity }
]

const sortOptions = [
  { value: 'relevant', label: 'Liên quan' },
  { value: 'price-asc', label: 'Giá thấp đến cao' },
  { value: 'price-desc', label: 'Giá cao đến thấp' },
  { value: 'name-asc', label: 'Tên A-Z' }
]

// Mock products data - In real app, this would come from API
const allProducts = ref([
  {
    id: 1,
    name: 'Arduino Uno R3 - Bo mạch phát triển',
    category: 'Arduino',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 200000,
    salePrice: 140000,
    discount: 30
  },
  {
    id: 2,
    name: 'ESP32 DevKit V1 - WiFi Bluetooth',
    category: 'ESP32',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 150000,
    salePrice: 105000,
    discount: 30
  },
  {
    id: 3,
    name: 'Raspberry Pi 4 Model B 4GB RAM',
    category: 'Raspberry Pi',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 1500000,
    salePrice: 1200000,
    discount: 20
  },
  {
    id: 4,
    name: 'LED RGB 5050 SMD - 100 chiếc',
    category: 'LED',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 100000,
    salePrice: 70000,
    discount: 30
  },
  {
    id: 5,
    name: 'Cảm biến nhiệt độ DHT11',
    category: 'Cảm biến',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 50000,
    salePrice: 35000,
    discount: 30
  },
  {
    id: 6,
    name: 'Motor DC 6V giảm tốc',
    category: 'Motor',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 80000,
    salePrice: 56000,
    discount: 30
  },
  {
    id: 7,
    name: 'Relay 5V 1 kênh',
    category: 'Relay',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 30000,
    salePrice: 21000,
    discount: 30
  },
  {
    id: 8,
    name: 'Màn hình LCD 16x2',
    category: 'Màn hình',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 120000,
    salePrice: 84000,
    discount: 30
  },
  {
    id: 9,
    name: 'Cảm biến siêu âm HC-SR04',
    category: 'Cảm biến',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 40000,
    salePrice: 28000,
    discount: 30
  },
  {
    id: 10,
    name: 'Module Bluetooth HC-05',
    category: 'Module',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 90000,
    salePrice: 63000,
    discount: 30
  },
  {
    id: 11,
    name: 'Arduino Nano V3.0 ATmega328P',
    category: 'Arduino',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 120000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 12,
    name: 'ESP8266 NodeMCU WiFi Module',
    category: 'ESP32',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 80000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 13,
    name: 'Điện trở 1/4W - Bộ 100 chiếc',
    category: 'Điện trở',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 30000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 14,
    name: 'Tụ điện Ceramic - Bộ 50 chiếc',
    category: 'Tụ điện',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 40000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 15,
    name: 'LED đơn 5mm - Bộ 100 chiếc',
    category: 'LED',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 25000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 16,
    name: 'Transistor 2N2222 - Bộ 10 chiếc',
    category: 'Transistor',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 15000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 17,
    name: 'IC 74LS00 Quad NAND Gate',
    category: 'IC 74LS',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 8000,
    salePrice: 0,
    discount: 0
  },
  {
    id: 18,
    name: 'Biến trở 10K Ohm',
    category: 'Biến trở',
    img: '/images/Edunera_Logo_512.png',
    originalPrice: 12000,
    salePrice: 0,
    discount: 0
  }
])

const filteredProducts = computed(() => {
  let filtered = allProducts.value

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.name.toLowerCase().includes(query) || 
      p.category.toLowerCase().includes(query)
    )
  }

  // Filter by category
  if (selectedCategory.value) {
    filtered = filtered.filter(p => p.category === selectedCategory.value)
  }

  // Filter by price range
  if (selectedPriceRange.value) {
    const range = priceRanges.find(r => r.id === selectedPriceRange.value)
    if (range) {
      filtered = filtered.filter(p => {
        const price = p.discount ? p.salePrice : p.originalPrice
        return price >= range.min && price <= range.max
      })
    }
  }

  // Filter by sale
  if (onlyShowSale.value) {
    filtered = filtered.filter(p => p.discount > 0)
  }

  return filtered
})

const sortedProducts = computed(() => {
  const products = [...filteredProducts.value]

  switch (sortBy.value) {
    case 'price-asc':
      return products.sort((a, b) => {
        const priceA = a.discount ? a.salePrice : a.originalPrice
        const priceB = b.discount ? b.salePrice : b.originalPrice
        return priceA - priceB
      })
    case 'price-desc':
      return products.sort((a, b) => {
        const priceA = a.discount ? a.salePrice : a.originalPrice
        const priceB = b.discount ? b.salePrice : b.originalPrice
        return priceB - priceA
      })
    case 'name-asc':
      return products.sort((a, b) => a.name.localeCompare(b.name))
    default:
      return products
  }
})

const totalPages = computed(() => Math.ceil(sortedProducts.value.length / itemsPerPage))

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return sortedProducts.value.slice(start, end)
})

const displayedPages = computed(() => {
  const pages = []
  const maxDisplay = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxDisplay / 2))
  let end = Math.min(totalPages.value, start + maxDisplay - 1)

  if (end - start < maxDisplay - 1) {
    start = Math.max(1, end - maxDisplay + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

const hasActiveFilters = computed(() => {
  return selectedCategory.value !== null || 
         selectedPriceRange.value !== null || 
         onlyShowSale.value
})

const handleSearch = () => {
  if (searchQuery.value !== route.query.q) {
    router.push({ path: '/search', query: { q: searchQuery.value } })
  }
}

const clearAllFilters = () => {
  selectedCategory.value = null
  selectedPriceRange.value = null
  onlyShowSale.value = false
  currentPage.value = 1
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const buyNow = (productId: number) => {
  // Implement buy now functionality
  alert(`Mua ngay sản phẩm ${productId}`)
}

const addToCart = (productId: number) => {
  // Implement add to cart functionality
  alert(`Đã thêm sản phẩm ${productId} vào giỏ hàng`)
}

// Reset page when filters change
watch([selectedCategory, selectedPriceRange, onlyShowSale, sortBy], () => {
  currentPage.value = 1
})

// Update search query when route changes
watch(() => route.query.q, (newQuery) => {
  searchQuery.value = String(newQuery || '')
  currentPage.value = 1
})

// Fetch categories from API on mount
onMounted(async () => {
  try {
    const data = await $fetch('http://localhost:8080/api/categories')
    categories.value = data as any[]
  } catch (error) {
    console.error('Error fetching categories:', error)
  }
})
</script>
