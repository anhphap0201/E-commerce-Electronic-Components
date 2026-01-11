<template>
  <div class="flex flex-col min-h-screen">
    <!-- Header -->
    <header class="border-b border-gray-200 sticky top-0 z-50 backdrop-blur-sm bg-white/95">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Top Row: Logo, Search, Auth -->
        <div class="flex items-center justify-between gap-4 py-4">
          <!-- Logo -->
          <NuxtLink to="/" class="flex items-center gap-2 group flex-shrink-0">
            <img 
              src="/images/Sub_Edunera_Logo_512.svg" 
              alt="Electronic Components Logo" 
              class="w-10 h-10 transition-all duration-300 group-hover:scale-110"
            />
            <span class="font-bold text-xl text-gray-800 hidden sm:block">ECShop</span>
          </NuxtLink>

          <!-- Search Bar -->
          <div class="flex-1 max-w-2xl">
            <div class="relative">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Tìm kiếm linh kiện điện tử..."
                class="w-full px-4 py-2.5 pr-12 border-2 border-gray-200 rounded-xl text-sm transition-all duration-300 outline-none focus:border-[#09f] focus:shadow-[0_0_0_3px_rgba(0,153,255,0.1)] hover:border-[#33adff] placeholder:text-gray-400"
                @keyup.enter="handleSearch"
              />
              <button
                @click="handleSearch"
                class="absolute right-2 top-1/2 -translate-y-1/2 w-8 h-8 bg-[#09f] rounded-lg flex items-center justify-center text-white transition-all duration-300 hover:bg-[#0077cc] hover:scale-105 active:scale-95"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Auth Buttons -->
          <div class="flex items-center gap-3 flex-shrink-0">
            <NuxtLink 
              to="/auth/auth" 
              class="px-4 py-2 text-sm font-medium text-[#09f] hover:text-[#0077cc] transition-colors duration-200 whitespace-nowrap"
            >
              Đăng nhập
            </NuxtLink>
          </div>
        </div>

        <!-- Popular Keywords -->
        <div class="pb-3 border-t border-gray-100 pt-3">
          <div class="flex items-center gap-2 flex-wrap">
            <span class="text-xs text-gray-500 whitespace-nowrap">Từ khóa phổ biến:</span>
            <button
              v-for="keyword in popularKeywords"
              :key="keyword"
              @click="searchQuery = keyword; handleSearch()"
              class="px-3 py-1 bg-gray-100 text-gray-700 text-xs rounded-full transition-all duration-200 hover:bg-[#09f] hover:text-white hover:scale-105"
            >
              {{ keyword }}
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-grow">
      <NuxtPage />
    </main>

    <!-- Footer -->
    <footer class="bg-gray-50 border-t border-gray-200 mt-auto">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <!-- Company Info -->
          <div class="col-span-1 md:col-span-2">
            <div class="flex items-center gap-2 mb-4">
              <img 
                src="/images/Sub_Edunera_Logo_512.svg" 
                alt="Electronic Components Logo" 
                class="w-10 h-10"
              />
              <span class="font-bold text-lg text-gray-800">ECShop</span>
            </div>
            <p class="text-sm text-gray-600 mb-4">
              Cung cấp các linh kiện điện tử chất lượng cao với giá cả cạnh tranh. Đội ngũ chuyên nghiệp, giao hàng nhanh chóng trên toàn quốc.
            </p>
            <div class="flex gap-3">
              <a href="#" class="w-9 h-9 flex items-center justify-center rounded-lg bg-gray-200 text-gray-600 hover:bg-[#09f] hover:text-white transition-all duration-300">
                <span class="text-lg">f</span>
              </a>
              <a href="#" class="w-9 h-9 flex items-center justify-center rounded-lg bg-gray-200 text-gray-600 hover:bg-[#09f] hover:text-white transition-all duration-300">
                <span class="text-lg">in</span>
              </a>
              <a href="#" class="w-9 h-9 flex items-center justify-center rounded-lg bg-gray-200 text-gray-600 hover:bg-[#09f] hover:text-white transition-all duration-300">
                <span class="text-lg">@</span>
              </a>
            </div>
          </div>

          <!-- Quick Links -->
          <div>
            <h3 class="font-semibold text-gray-800 mb-4">Liên kết nhanh</h3>
            <ul class="space-y-2">
              <li>
                <NuxtLink to="/" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Trang chủ</NuxtLink>
              </li>
              <li>
                <NuxtLink to="/products" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Sản phẩm</NuxtLink>
              </li>
              <li>
                <NuxtLink to="/about" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Về chúng tôi</NuxtLink>
              </li>
              <li>
                <NuxtLink to="/contact" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Liên hệ</NuxtLink>
              </li>
            </ul>
          </div>

          <!-- Support -->
          <div>
            <h3 class="font-semibold text-gray-800 mb-4">Hỗ trợ</h3>
            <ul class="space-y-2">
              <li>
                <a href="#" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Chính sách bảo hành</a>
              </li>
              <li>
                <a href="#" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Chính sách đổi trả</a>
              </li>
              <li>
                <a href="#" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Phương thức thanh toán</a>
              </li>
              <li>
                <a href="#" class="text-sm text-gray-600 hover:text-[#09f] transition-colors duration-200">Câu hỏi thường gặp</a>
              </li>
            </ul>
          </div>
        </div>

        <!-- Copyright -->
        <div class="border-t border-gray-200 mt-8 pt-8 text-center">
          <p class="text-sm text-gray-600">
            © {{ new Date().getFullYear() }} Electronic Components. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const searchQuery = ref('')

const popularKeywords = [
  'Arduino',
  'ESP32',
  'Raspberry Pi',
  'IC 74LS',
  'LED',
  'Cảm biến',
  'Motor',
  'Relay'
]

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    navigateTo(`/search?q=${encodeURIComponent(searchQuery.value)}`)
  }
}
</script>