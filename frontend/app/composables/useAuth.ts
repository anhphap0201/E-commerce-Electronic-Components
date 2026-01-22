import { ref, computed, onMounted } from 'vue'

interface User {
  email: string
  role?: string
  [key: string]: any
}

const user = ref<User | null>(null)
const token = ref<string | null>(null)
const isInitialized = ref(false)

const isAuthenticated = computed(() => !!user.value && !!token.value)

// Initialize function to be called on client
const initializeAuth = () => {
  console.log('🚀 initializeAuth called, isInitialized:', isInitialized.value, 'process.client:', process.client)
  if (isInitialized.value || !process.client) return
  
  isInitialized.value = true
  const storedToken = localStorage.getItem('token')
  const storedUser = localStorage.getItem('user')
  
  console.log('📦 localStorage:', { storedToken, storedUser })
  
  if (storedToken && storedUser) {
    try {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
      console.log('✅ Initialized from localStorage:', user.value, 'isAuth:', isAuthenticated.value)
    } catch (e) {
      console.error('Error parsing stored user:', e)
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
}

export const useAuth = () => {
  // Auto-initialize on mount
  if (process.client && !isInitialized.value) {
    initializeAuth()
  }

  const login = (userData: User, authToken: string) => {
    console.log('🔐 LOGIN CALLED:', userData, authToken)
    user.value = userData
    token.value = authToken
    console.log('✅ After login - user:', user.value, 'isAuth:', isAuthenticated.value)
    
    if (process.client) {
      localStorage.setItem('token', authToken)
      localStorage.setItem('user', JSON.stringify(userData))
      console.log('💾 Saved to localStorage')
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    
    if (process.client) {
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
