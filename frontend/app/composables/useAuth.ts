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
  if (isInitialized.value || !process.client) return
  
  isInitialized.value = true
  const storedToken = localStorage.getItem('token')
  const storedUser = localStorage.getItem('user')
  
  if (storedToken && storedUser) {
    try {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
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
    user.value = userData
    token.value = authToken
    
    if (process.client) {
      localStorage.setItem('token', authToken)
      localStorage.setItem('user', JSON.stringify(userData))
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
