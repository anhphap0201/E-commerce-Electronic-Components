# TEST GUEST CART

## Bước 1: Xóa localStorage cũ
Mở DevTools (F12) → Console → chạy:
```javascript
localStorage.clear()
location.reload()
```

## Bước 2: Kiểm tra guest cart
1. KHÔNG đăng nhập
2. Vào trang sản phẩm bất kỳ
3. Nhấn "Thêm vào giỏ hàng"
4. Kiểm tra localStorage:
```javascript
JSON.parse(localStorage.getItem('guest_cart'))
```

## Bước 3: Xem giỏ hàng  
1. Vào trang /cart
2. Xem danh sách sản phẩm
3. Kiểm tra console xem có lỗi không

## Debug Commands
Check guest cart:
```javascript
console.log('Guest cart:', JSON.parse(localStorage.getItem('guest_cart')))
```

Check auth status:
```javascript
console.log('Token:', localStorage.getItem('token'))
console.log('User:', localStorage.getItem('user'))
```
