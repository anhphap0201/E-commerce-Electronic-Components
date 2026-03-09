<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-6 gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p class="text-gray-500 text-sm">{{ currentDateFormatted }}</p>
        </div>
        <button @click="fetchDashboard" :disabled="loading"
          class="inline-flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50">
          <svg class="w-4 h-4" :class="{ 'animate-spin': loading }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Làm mới
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading && !dashboard" class="flex items-center justify-center py-32">
        <div class="text-center">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#09f] mx-auto mb-4"></div>
          <p class="text-gray-500">Đang tải dữ liệu...</p>
        </div>
      </div>

      <template v-else-if="dashboard">
        <!-- Tabs -->
        <div class="flex items-center gap-1 mb-6 bg-white rounded-xl border border-gray-200 p-1 overflow-x-auto">
          <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
            class="flex items-center gap-2 px-4 py-2.5 rounded-lg text-sm font-medium transition-all whitespace-nowrap"
            :class="activeTab === tab.key
              ? 'bg-[#09f] text-white shadow-sm'
              : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'">
            <component :is="tab.icon" class="w-4 h-4" />
            {{ tab.label }}
          </button>
        </div>

        <!-- ==================== TAB: Tổng quan ==================== -->
        <div v-show="activeTab === 'overview'">
          <!-- Summary cards -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            <div class="bg-white rounded-xl border border-gray-200 p-4">
              <p class="text-xs text-gray-500 mb-1">Tổng doanh thu</p>
              <p class="text-xl font-bold text-gray-900">{{ formatPrice(dashboard.totalRevenue) }}</p>
            </div>
            <div class="bg-white rounded-xl border border-gray-200 p-4">
              <p class="text-xs text-gray-500 mb-1">Đơn hàng</p>
              <p class="text-xl font-bold text-gray-900">{{ dashboard.totalOrders }}</p>
            </div>
            <div class="bg-white rounded-xl border border-gray-200 p-4">
              <p class="text-xs text-gray-500 mb-1">Sản phẩm</p>
              <p class="text-xl font-bold text-gray-900">{{ dashboard.totalProducts }}</p>
            </div>
            <div class="bg-white rounded-xl border border-gray-200 p-4">
              <p class="text-xs text-gray-500 mb-1">Người dùng</p>
              <p class="text-xl font-bold text-gray-900">{{ dashboard.totalUsers }}</p>
            </div>
          </div>

          <!-- Period stats -->
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            <div v-for="period in periodCards" :key="period.label"
              class="bg-white rounded-xl border border-gray-200 p-4 hover:shadow-md transition-shadow">
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs font-semibold text-gray-500 uppercase tracking-wider">{{ period.label }}</span>
                <div class="w-7 h-7 rounded-lg flex items-center justify-center" :class="period.iconBg">
                  <svg class="w-3.5 h-3.5" :class="period.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="period.iconPath" />
                  </svg>
                </div>
              </div>
              <p class="text-lg font-bold text-gray-900">{{ formatPrice(period.data?.revenue) }}</p>
              <p class="text-xs text-gray-500 mt-1">{{ period.data?.orderCount || 0 }} đơn &middot; {{ period.data?.newUsers || 0 }} user mới</p>
            </div>
          </div>

          <!-- Quick Access -->
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <NuxtLink v-for="link in quickLinks" :key="link.to" :to="link.to" class="group">
              <div class="bg-white rounded-xl border border-gray-200 p-4 hover:shadow-md hover:border-[#09f] transition-all text-center">
                <div class="w-10 h-10 rounded-xl flex items-center justify-center mx-auto mb-2 transition-colors"
                  :class="link.bg + ' group-hover:bg-[#09f]'">
                  <svg class="w-5 h-5 group-hover:text-white transition-colors" :class="link.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="link.iconPath" />
                  </svg>
                </div>
                <h4 class="font-semibold text-gray-900 text-sm">{{ link.label }}</h4>
              </div>
            </NuxtLink>
          </div>
        </div>

        <!-- ==================== TAB: Doanh thu ==================== -->
        <div v-show="activeTab === 'revenue'">
          <!-- Revenue chart -->
          <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
              <div>
                <h3 class="text-base font-semibold text-gray-900">Doanh thu & Đơn hàng</h3>
                <p class="text-xs text-gray-500 mt-0.5">Tổng: {{ formatPrice(totalRevenueInChart) }} &middot; {{ totalOrdersInChart }} đơn</p>
              </div>
              <div class="flex items-center gap-1.5">
                <button v-for="r in chartRanges" :key="r.days" @click="changeChartRange(r.days)"
                  class="px-3 py-1.5 rounded-lg text-xs font-medium transition-all"
                  :class="chartDays === r.days
                    ? 'bg-[#09f] text-white shadow-sm'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                  :disabled="chartLoading">{{ r.label }}</button>
                <div class="flex items-center gap-3 ml-3 text-xs text-gray-400">
                  <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-[#09f]"></span> DT</span>
                  <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-emerald-400"></span> ĐH</span>
                </div>
              </div>
            </div>
            <div v-if="chartLoading" class="flex items-center justify-center h-48">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#09f]"></div>
            </div>
            <div v-else-if="chartData.length" class="relative">
              <div class="flex items-end gap-[2px] h-48 mb-2">
                <div v-for="(day, idx) in chartData" :key="idx"
                  class="flex-1 flex flex-col items-center justify-end gap-[1px] group relative">
                  <div class="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 bg-gray-800 text-white text-xs rounded-lg px-3 py-2 whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10 shadow-lg">
                    <p class="font-medium">{{ formatChartDate(day.date) }}</p>
                    <p>Doanh thu: {{ formatPrice(day.revenue) }}</p>
                    <p>Đơn hàng: {{ day.orderCount }}</p>
                  </div>
                  <div class="w-full rounded-t bg-[#09f]/80 hover:bg-[#09f] transition-all cursor-pointer min-h-[2px]"
                    :style="{ height: revenueBarHeight(day.revenue) + '%' }"></div>
                  <div class="w-full rounded-t bg-emerald-400/80 hover:bg-emerald-400 transition-all cursor-pointer min-h-[1px]"
                    :style="{ height: orderBarHeight(day.orderCount) + '%' }"></div>
                </div>
              </div>
              <div class="flex gap-[2px]">
                <div v-for="(day, idx) in chartData" :key="'l-' + idx" class="flex-1 text-center">
                  <span v-if="idx % chartLabelInterval === 0 || idx === chartData.length - 1" class="text-[10px] text-gray-400">{{ day.date.slice(5) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="flex items-center justify-center h-48 text-gray-400 text-sm">Chưa có dữ liệu</div>
          </div>

          <!-- Top sản phẩm bán chạy theo doanh thu (30 ngày) -->
          <div class="bg-white rounded-xl border border-gray-200 p-6">
            <h3 class="text-base font-semibold text-gray-900 mb-4">Top sản phẩm theo doanh thu (30 ngày)</h3>
            <div v-if="dashboard.topProducts?.length" class="space-y-2">
              <div v-for="(product, idx) in dashboard.topProducts" :key="idx"
                class="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                <div class="w-7 h-7 rounded-lg flex items-center justify-center font-bold text-xs"
                  :class="idx < 3 ? 'bg-[#09f] text-white' : 'bg-gray-100 text-gray-600'">{{ idx + 1 }}</div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-gray-900 truncate">{{ product.productName }}</p>
                  <p class="text-xs text-gray-500 truncate">{{ product.variantName }}</p>
                </div>
                <div class="text-right flex-shrink-0">
                  <p class="text-sm font-bold text-gray-900">{{ product.totalQuantity }} sp</p>
                  <p class="text-xs text-gray-500">{{ formatPrice(product.totalRevenue) }}</p>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-8 text-gray-400 text-sm">Chưa có dữ liệu</div>
          </div>
        </div>

        <!-- ==================== TAB: Đơn hàng ==================== -->
        <div v-show="activeTab === 'orders'">
          <!-- Order chart -->
          <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4">
              <div>
                <h3 class="text-base font-semibold text-gray-900">Đơn hàng theo ngày</h3>
                <p class="text-xs text-gray-500 mt-0.5">Tổng: <span class="font-bold text-gray-900">{{ totalOrdersInChart }}</span> đơn</p>
              </div>
              <div class="flex items-center gap-1.5">
                <button v-for="r in chartRanges" :key="'ord-' + r.days" @click="changeChartRange(r.days)"
                  class="px-3 py-1.5 rounded-lg text-xs font-medium transition-all"
                  :class="chartDays === r.days
                    ? 'bg-indigo-500 text-white shadow-sm'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                  :disabled="chartLoading">{{ r.label }}</button>
              </div>
            </div>
            <div v-if="chartLoading" class="flex items-center justify-center h-44">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
            </div>
            <div v-else-if="chartData.length" class="relative">
              <div class="flex items-end gap-[2px] h-44 mb-2">
                <div v-for="(day, idx) in chartData" :key="'o-' + idx"
                  class="flex-1 flex flex-col items-center justify-end group relative">
                  <div class="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 bg-gray-800 text-white text-xs rounded-lg px-3 py-2 whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10 shadow-lg">
                    <p class="font-medium">{{ formatChartDate(day.date) }}</p>
                    <p>Đơn hàng: {{ day.orderCount }}</p>
                    <p>Doanh thu: {{ formatPrice(day.revenue) }}</p>
                  </div>
                  <div class="w-full rounded-t bg-indigo-500/80 hover:bg-indigo-500 transition-all cursor-pointer min-h-[2px]"
                    :style="{ height: orderChartBarHeight(day.orderCount) + '%' }"></div>
                </div>
              </div>
              <div class="flex gap-[2px]">
                <div v-for="(day, idx) in chartData" :key="'ol-' + idx" class="flex-1 text-center">
                  <span v-if="idx % chartLabelInterval === 0 || idx === chartData.length - 1" class="text-[10px] text-gray-400">{{ day.date.slice(5) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="flex items-center justify-center h-44 text-gray-400 text-sm">Chưa có dữ liệu</div>
          </div>

          <!-- Order status summary -->
          <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
            <h3 class="text-base font-semibold text-gray-900 mb-4">Trạng thái đơn hàng</h3>
            <div class="grid grid-cols-5 gap-3 mb-4">
              <div v-for="s in orderStatuses" :key="s.key" class="text-center p-3 rounded-xl" :class="s.bg">
                <p class="text-2xl font-bold" :class="s.color">{{ dashboard[s.key] }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ s.label }}</p>
              </div>
            </div>
            <div class="h-3 rounded-full overflow-hidden flex bg-gray-100" v-if="dashboard.totalOrders > 0">
              <div v-for="s in orderStatuses" :key="'bar-' + s.key" :class="s.barColor" class="transition-all"
                :style="{ width: statusPercent(s.statusKey) + '%' }" :title="`${s.label}: ${dashboard[s.key]}`"></div>
            </div>
          </div>

          <!-- Recent orders -->
          <div class="bg-white rounded-xl border border-gray-200 p-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-base font-semibold text-gray-900">Đơn hàng gần đây</h3>
              <NuxtLink to="/admin/orders" class="text-sm text-[#09f] hover:underline">Xem tất cả</NuxtLink>
            </div>
            <div v-if="dashboard.recentOrders?.length" class="space-y-2">
              <div v-for="order in dashboard.recentOrders" :key="order.id"
                class="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2">
                    <span class="text-sm font-medium text-[#09f]">{{ order.orderNumber || `#${order.id}` }}</span>
                    <span :class="getStatusClass(order.status)" class="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-medium">
                      {{ getStatusLabel(order.status) }}
                    </span>
                  </div>
                  <p class="text-xs text-gray-500 mt-0.5">{{ order.fullName }} &middot; {{ formatDate(order.createdAt) }}</p>
                </div>
                <p class="text-sm font-bold text-gray-900 flex-shrink-0">{{ formatPrice(order.totalCost) }}</p>
              </div>
            </div>
            <div v-else class="text-center py-8 text-gray-400 text-sm">Chưa có đơn hàng nào</div>
          </div>
        </div>

        <!-- ==================== TAB: Sản phẩm ==================== -->
        <div v-show="activeTab === 'products'">
          <!-- Stats cards -->
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
            <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl shadow-sm p-5 text-white">
              <p class="text-emerald-100 text-xs mb-1">Tổng đã bán</p>
              <p class="text-2xl font-bold">{{ dashboard.productStats?.totalSoldQuantity?.toLocaleString('vi-VN') || 0 }}</p>
              <p class="text-emerald-200 text-xs mt-0.5">sản phẩm</p>
            </div>
            <div class="bg-gradient-to-br from-amber-500 to-orange-600 rounded-xl shadow-sm p-5 text-white">
              <p class="text-amber-100 text-xs mb-1">Đánh giá TB</p>
              <p class="text-2xl font-bold">{{ dashboard.productStats?.averageRating || 0 }} <span class="text-base font-normal text-amber-200">/ 5</span></p>
            </div>
            <div class="bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl shadow-sm p-5 text-white">
              <p class="text-blue-100 text-xs mb-1">Tổng sản phẩm</p>
              <p class="text-2xl font-bold">{{ dashboard.totalProducts }}</p>
              <p class="text-blue-200 text-xs mt-0.5">{{ dashboard.totalCategories }} danh mục</p>
            </div>
          </div>

          <!-- Rating distribution -->
          <div v-if="dashboard.productStats?.ratingDistribution" class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
            <h3 class="text-base font-semibold text-gray-900 mb-4">Phân bố đánh giá</h3>
            <div class="space-y-2.5">
              <div v-for="(item, idx) in ratingBars" :key="idx" class="flex items-center gap-3">
                <span class="text-sm font-medium text-gray-600 w-16 flex-shrink-0">{{ item.label }}</span>
                <div class="flex-1 h-5 bg-gray-100 rounded-full overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-500" :class="item.color"
                    :style="{ width: ratingBarWidth(item.count) + '%' }"></div>
                </div>
                <span class="text-sm font-bold text-gray-700 w-8 text-right flex-shrink-0">{{ item.count }}</span>
              </div>
            </div>
          </div>

          <!-- Top selling & Top rated -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Top bán chạy -->
            <div class="bg-white rounded-xl border border-gray-200 p-6">
              <div class="flex items-center gap-2 mb-4">
                <svg class="w-4 h-4 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                </svg>
                <h3 class="text-base font-semibold text-gray-900">Top bán chạy</h3>
              </div>
              <div v-if="dashboard.productStats?.topSellingProducts?.length" class="space-y-1.5">
                <div v-for="(p, idx) in dashboard.productStats.topSellingProducts" :key="p.productId"
                  class="flex items-center gap-3 p-2.5 rounded-lg hover:bg-gray-50 transition-colors">
                  <div class="w-7 h-7 rounded-lg flex items-center justify-center font-bold text-xs flex-shrink-0"
                    :class="idx < 3 ? 'bg-emerald-500 text-white' : 'bg-gray-100 text-gray-500'">{{ idx + 1 }}</div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">{{ p.productName }}</p>
                    <span class="text-xs text-amber-500 flex items-center gap-0.5">
                      <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>
                      {{ p.avgRating }}
                    </span>
                  </div>
                  <p class="text-sm font-bold text-emerald-600 flex-shrink-0">{{ p.soldQuantity?.toLocaleString('vi-VN') }}</p>
                </div>
              </div>
              <div v-else class="text-center py-8 text-gray-400 text-sm">Chưa có dữ liệu</div>
            </div>

            <!-- Top đánh giá -->
            <div class="bg-white rounded-xl border border-gray-200 p-6">
              <div class="flex items-center gap-2 mb-4">
                <svg class="w-4 h-4 text-amber-500" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
                <h3 class="text-base font-semibold text-gray-900">Top đánh giá cao</h3>
              </div>
              <div v-if="dashboard.productStats?.topRatedProducts?.length" class="space-y-1.5">
                <div v-for="(p, idx) in dashboard.productStats.topRatedProducts" :key="p.productId"
                  class="flex items-center gap-3 p-2.5 rounded-lg hover:bg-gray-50 transition-colors">
                  <div class="w-7 h-7 rounded-lg flex items-center justify-center font-bold text-xs flex-shrink-0"
                    :class="idx < 3 ? 'bg-amber-500 text-white' : 'bg-gray-100 text-gray-500'">{{ idx + 1 }}</div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">{{ p.productName }}</p>
                    <p class="text-xs text-gray-500">Đã bán: {{ p.soldQuantity?.toLocaleString('vi-VN') || 0 }}</p>
                  </div>
                  <div class="flex items-center gap-1 flex-shrink-0">
                    <svg class="w-4 h-4 text-amber-400" fill="currentColor" viewBox="0 0 20 20"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>
                    <span class="text-sm font-bold text-amber-600">{{ p.avgRating }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="text-center py-8 text-gray-400 text-sm">Chưa có dữ liệu</div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, h } from 'vue'
import { useAuth } from '~/composables/useAuth'
import { useNotification } from '~/composables/useNotification'

definePageMeta({ middleware: 'admin' })

const { user, token } = useAuth()
const { error } = useNotification()

const loading = ref(false)
const dashboard = ref<any>(null)
const activeTab = ref('overview')
const chartData = ref<any[]>([])
const chartDays = ref(30)
const chartLoading = ref(false)

const chartRanges = [
  { label: 'Tuần', days: 7 },
  { label: 'Tháng', days: 30 },
  { label: 'Quý', days: 90 },
  { label: 'Năm', days: 365 },
]

// --- Tab definitions ---
const SvgIcon = (path: string) => ({
  render: () => h('svg', { class: 'w-4 h-4', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' },
    [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: path })])
})

const tabs = [
  { key: 'overview', label: 'Tổng quan', icon: SvgIcon('M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0h4') },
  { key: 'revenue', label: 'Doanh thu', icon: SvgIcon('M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z') },
  { key: 'orders', label: 'Đơn hàng', icon: SvgIcon('M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2') },
  { key: 'products', label: 'Sản phẩm', icon: SvgIcon('M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4') },
]

// --- Static data ---
const orderStatuses = [
  { key: 'pendingOrders', statusKey: 'pending', label: 'Chờ xử lý', bg: 'bg-yellow-50', color: 'text-yellow-600', barColor: 'bg-yellow-400' },
  { key: 'confirmedOrders', statusKey: 'confirmed', label: 'Đã xác nhận', bg: 'bg-blue-50', color: 'text-blue-600', barColor: 'bg-blue-400' },
  { key: 'shippedOrders', statusKey: 'shipped', label: 'Đang giao', bg: 'bg-purple-50', color: 'text-purple-600', barColor: 'bg-purple-400' },
  { key: 'deliveredOrders', statusKey: 'delivered', label: 'Đã giao', bg: 'bg-green-50', color: 'text-green-600', barColor: 'bg-green-500' },
  { key: 'cancelledOrders', statusKey: 'cancelled', label: 'Đã hủy', bg: 'bg-red-50', color: 'text-red-600', barColor: 'bg-red-400' },
]

const quickLinks = [
  { to: '/admin/products', label: 'Sản phẩm', bg: 'bg-blue-100', iconColor: 'text-blue-600', iconPath: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4' },
  { to: '/admin/categories', label: 'Danh mục', bg: 'bg-purple-100', iconColor: 'text-purple-600', iconPath: 'M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z' },
  { to: '/admin/orders', label: 'Đơn hàng', bg: 'bg-green-100', iconColor: 'text-green-600', iconPath: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
  { to: '/admin/users', label: 'Người dùng', bg: 'bg-orange-100', iconColor: 'text-orange-600', iconPath: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' },
]

// --- Computed ---
const currentDateFormatted = computed(() =>
  new Date().toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
)

const periodCards = computed(() => [
  { label: 'Hôm nay', data: dashboard.value?.today, iconBg: 'bg-blue-100', iconColor: 'text-blue-600', iconPath: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z' },
  { label: 'Tuần này', data: dashboard.value?.thisWeek, iconBg: 'bg-green-100', iconColor: 'text-green-600', iconPath: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z' },
  { label: 'Tháng này', data: dashboard.value?.thisMonth, iconBg: 'bg-purple-100', iconColor: 'text-purple-600', iconPath: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' },
  { label: 'Năm này', data: dashboard.value?.thisYear, iconBg: 'bg-orange-100', iconColor: 'text-orange-600', iconPath: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6' },
])

const ratingBars = computed(() => {
  const dist = dashboard.value?.productStats?.ratingDistribution
  if (!dist) return []
  return [
    { label: '5 sao', count: dist.fiveStar || 0, color: 'bg-emerald-500' },
    { label: '4 sao', count: dist.fourStar || 0, color: 'bg-green-400' },
    { label: '3 sao', count: dist.threeStar || 0, color: 'bg-yellow-400' },
    { label: '2 sao', count: dist.twoStar || 0, color: 'bg-orange-400' },
    { label: '1 sao', count: dist.oneStar || 0, color: 'bg-red-400' },
    { label: 'Chưa có', count: dist.noRating || 0, color: 'bg-gray-300' },
  ]
})

const maxRatingCount = computed(() => {
  if (!ratingBars.value.length) return 1
  return Math.max(1, ...ratingBars.value.map(b => b.count))
})

// --- Chart helpers ---
const maxRevenue = computed(() => {
  if (!chartData.value.length) return 1
  return Math.max(1, ...chartData.value.map((d: any) => Number(d.revenue) || 0))
})

const maxOrders = computed(() => {
  if (!chartData.value.length) return 1
  return Math.max(1, ...chartData.value.map((d: any) => d.orderCount || 0))
})

const revenueBarHeight = (revenue: number | null) => !revenue ? 1 : Math.max(1, (Number(revenue) / maxRevenue.value) * 80)
const orderBarHeight = (count: number) => !count ? 1 : Math.max(1, (count / maxOrders.value) * 20)
const orderChartBarHeight = (count: number) => !count ? 2 : Math.max(2, (count / maxOrders.value) * 95)
const ratingBarWidth = (count: number) => !count ? 0 : Math.max(2, (count / maxRatingCount.value) * 100)

const totalOrdersInChart = computed(() => {
  if (!chartData.value.length) return 0
  return chartData.value.reduce((sum: number, d: any) => sum + (d.orderCount || 0), 0)
})

const totalRevenueInChart = computed(() => {
  if (!chartData.value.length) return 0
  return chartData.value.reduce((sum: number, d: any) => sum + (Number(d.revenue) || 0), 0)
})

const chartLabelInterval = computed(() => {
  const len = chartData.value.length
  if (len <= 14) return 1
  if (len <= 31) return 5
  if (len <= 100) return 10
  return 30
})

const statusPercent = (status: string) => {
  if (!dashboard.value?.totalOrders) return 0
  const map: Record<string, string> = { pending: 'pendingOrders', confirmed: 'confirmedOrders', shipped: 'shippedOrders', delivered: 'deliveredOrders', cancelled: 'cancelledOrders' }
  return ((dashboard.value[map[status]] || 0) / dashboard.value.totalOrders * 100).toFixed(1)
}

// --- Formatting ---
const formatPrice = (price: number | null) => {
  if (!price) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(Number(price))
}

const formatDate = (dateStr: string | null) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const formatChartDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString('vi-VN', { weekday: 'short', day: '2-digit', month: '2-digit' })

const getStatusLabel = (status: string) => ({ PENDING: 'Chờ xử lý', CONFIRMED: 'Đã xác nhận', SHIPPED: 'Đang giao', DELIVERED: 'Đã giao', CANCELLED: 'Đã hủy' }[status] || status)

const getStatusClass = (status: string) => ({
  PENDING: 'bg-yellow-100 text-yellow-800', CONFIRMED: 'bg-blue-100 text-blue-800', SHIPPED: 'bg-purple-100 text-purple-800',
  DELIVERED: 'bg-green-100 text-green-800', CANCELLED: 'bg-red-100 text-red-800'
}[status] || 'bg-gray-100 text-gray-800')

// --- Data fetching ---
onMounted(async () => {
  if (user.value?.role !== 'ROLE_ADMIN') {
    error('Truy cập bị từ chối', 'Bạn không có quyền truy cập trang này')
    navigateTo('/')
    return
  }
  await fetchDashboard()
})

const changeChartRange = async (days: number) => {
  chartDays.value = days
  await fetchChartData()
}

const fetchChartData = async () => {
  chartLoading.value = true
  try {
    chartData.value = await $fetch<any[]>(`http://localhost:8080/api/admin/chart?days=${chartDays.value}`, {
      headers: { Authorization: `Bearer ${token.value}` }
    })
  } catch (e) {
    console.error('Failed to fetch chart data:', e)
  } finally {
    chartLoading.value = false
  }
}

const fetchDashboard = async () => {
  loading.value = true
  try {
    dashboard.value = await $fetch<any>('http://localhost:8080/api/admin/dashboard', {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    chartData.value = dashboard.value.revenueChart || []
  } catch (e: any) {
    console.error('Failed to fetch dashboard:', e)
    try {
      const data = await $fetch<any>('http://localhost:8080/api/admin/stats', {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      dashboard.value = {
        ...data,
        today: { orderCount: 0, revenue: 0, newUsers: 0 },
        thisWeek: { orderCount: 0, revenue: 0, newUsers: 0 },
        thisMonth: { orderCount: 0, revenue: 0, newUsers: 0 },
        thisYear: { orderCount: 0, revenue: 0, newUsers: 0 },
        revenueChart: [],
        topProducts: [],
      }
    } catch (e2) {
      console.error('Fallback also failed:', e2)
      error('Lỗi', 'Không thể tải dữ liệu dashboard')
    }
  } finally {
    loading.value = false
  }
}
</script>
