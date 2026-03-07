import { ref, computed, onMounted } from 'vue'

interface User {
  email: string
  fullName?: string
  role?: string
  [key: string]: any
}

const user = ref<User | null>(null)
const token = ref<string | null>(null)
const isInitialized = ref(false)

const isAuthenticated = computed(() => !!user.value && !!token.value)

// Initialize function to be called on client
const initializeAuth = () => {
  if (isInitialized.value || !import.meta.client) return
  
  console.log('🔐 Initializing auth...')
  isInitialized.value = true
  const storedToken = localStorage.getItem('token')
  const storedUser = localStorage.getItem('user')
  
  console.log('🔑 Stored token:', storedToken ? `${storedToken.substring(0, 20)}...` : 'NO TOKEN')
  console.log('👤 Stored user:', storedUser ? JSON.parse(storedUser).email : 'NO USER')
  
  if (storedToken && storedUser) {
    try {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
      console.log('✅ Auth initialized successfully')
    } catch (e) {
      console.error('❌ Error parsing stored user:', e)
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  } else {
    console.warn('⚠️ No stored auth data found')
  }
}

export const useAuth = () => {
  // Auto-initialize on mount
  if (import.meta.client && !isInitialized.value) {
    initializeAuth()
  }

  const login = (userData: User, authToken: string) => {
    console.log('🔐 Login called')
    console.log('👤 User data:', userData.email)
    console.log('🔑 Token:', authToken ? `${authToken.substring(0, 20)}...` : 'NO TOKEN')
    
    user.value = userData
    token.value = authToken
    
    if (import.meta.client) {
      localStorage.setItem('token', authToken)
      localStorage.setItem('user', JSON.stringify(userData))
      console.log('✅ Auth data saved to localStorage')
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    
    if (import.meta.client) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  const getAuthHeader = () => {
    return token.value ? { Authorization: `Bearer ${token.value}` } : {}
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    logout,
    getAuthHeader,
    initializeAuth
  }
}
