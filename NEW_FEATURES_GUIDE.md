# New Features Quick Start Guide

## 🎉 What's New

This update adds three major features to the Zeus Hotel Smart Convenience Store system:

### 1. Three-Tier User Role System
- **Ordinary Users (普通用户)**: Default role for new registrations
- **Employees (员工)**: Get employee discount prices
- **Administrators (管理员)**: Can access admin panel and distribute vouchers

### 2. Admin Voucher Distribution
Administrators can now directly distribute vouchers to specific users through the admin panel.

### 3. Chinese-English Language Switching
Complete bilingual support with one-click language switching.

---

## 🚀 Getting Started

### Database Setup

If you already have the database, run this migration:

```sql
-- Add user role column
ALTER TABLE tbl_shopper_profiles 
ADD COLUMN user_role TINYINT DEFAULT 0 
COMMENT '用户角色 0-普通用户 1-员工 2-管理员';

-- Set admin user's role
UPDATE tbl_shopper_profiles 
SET user_role = 2 
WHERE login_username = 'admin';
```

For new installations, just run the updated `schema.sql` file.

### Starting the Application

**Backend:**
```bash
cd ZeusHotelShop-Backend
mvn spring-boot:run
```
Backend runs on: http://localhost:9876/zeus-api

**Frontend:**
```bash
cd ZeusShop-VueInterface
npm install  # First time only
npm run dev
```
Frontend runs on: http://localhost:5173

---

## 📱 How to Use New Features

### Language Switching
1. Look for the language switcher button (🌐 中文 / EN) in the header
2. Click to toggle between Chinese and English
3. Your preference is automatically saved

### Admin Panel (Administrators Only)
1. Login with admin account
2. Look for the ⚙️ (gear) icon in the top navigation
3. Click to access the admin panel

### Distributing Vouchers (Admins)
1. Navigate to the Admin Panel
2. Select a voucher from the dropdown menu
3. Find the user you want to distribute to
4. Click "分发优惠券" (Distribute Voucher) button
5. Confirm the distribution

---

## 👥 User Accounts

### Default Admin Account
- **Username**: `admin`
- **Password**: Check the database or register a new admin

### Creating New Users
1. Go to the registration page
2. Fill in the form (username, password, nickname, phone)
3. New users are automatically set as "Ordinary Users"
4. Admins can manually change user roles in the database

### User Role Codes
- `0` = Ordinary User (普通用户)
- `1` = Employee (员工)
- `2` = Administrator (管理员)

---

## 🎨 UI Updates

### Header Navigation
- **Language Switcher**: Always visible in the header
- **Admin Icon**: Only visible to administrators (role=2)
- **Shopping Cart Icon**: Shows item count badge
- **Profile Icon**: Access user profile

### Admin Panel Features
- **User Management**: View all users with their roles
- **Voucher Selection**: Dropdown to select voucher for distribution
- **User Cards**: Shows avatar, name, username, role badge, and phone
- **Distribution Confirmation**: Modal dialog to confirm before distributing

---

## 🔧 Technical Details

### Role-Based Visibility
The admin icon appears only when:
```javascript
shopperStore.currentUser?.userRole === 2
```

### Language Persistence
Selected language is saved to localStorage:
```javascript
localStorage.setItem('locale', newLocale)
```

### API Endpoints (New)
```
GET  /gateway/shopper-accounts/admin/list-all
POST /gateway/vouchers/admin/distribute/{voucherId}/to/{targetUserId}
```

---

## 🐛 Troubleshooting

### Language Not Changing
- Clear browser cache and reload
- Check browser console for errors

### Admin Button Not Showing
- Verify user role in database: `SELECT user_role FROM tbl_shopper_profiles WHERE login_username = 'your_username';`
- Should be `2` for administrators

### Cannot Distribute Vouchers
- Ensure you're logged in as admin (role=2)
- Select a voucher before clicking distribute
- User cannot already have the voucher

---

## 📊 System Requirements

- **Backend**: Java 17+, MySQL 8.0+, Maven 3.6+
- **Frontend**: Node.js 18+
- **Browser**: Modern browser with JavaScript enabled

---

## 📝 Notes

- All new users default to Ordinary User role
- Employee role (1) must be set manually in database for now
- Category filtering uses Chinese values internally (backend requirement)
- Translation coverage is comprehensive but can be extended by editing JSON files

---

**For more details, see** `IMPLEMENTATION_SUMMARY.md`
