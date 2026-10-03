import request from '@/utils/request'

export interface LoginRequest {
  appName: string
  email: string
  username: string
  password: string
}

export interface RegisterRequest {
  appName: string
  email: string
  username: string
  password: string
  realName?: string
  phone?: string
  photo?: string // base64
}

export interface UserInfo {
  id: number
  username: string
  realName?: string
  email?: string
  phone?: string
  photo?: string
  appId?: number
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface RegisterResponse {
  id: number
  username: string
  realName?: string
  email?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}


export interface ProfileResponse {
  message: string
}

/**
 * 登录
 */
export function login(data: LoginRequest): Promise<LoginResponse> {
  return request.post('/auth/login', data)
}

/**
 * 注册
 */
export function register(data: RegisterRequest): Promise<RegisterResponse> {
  return request.post('/auth/register', data)
}

/**
 * 退出登录（如果后端有对应接口）
 */
export function logout(): Promise<void> {
  return request.post('/auth/logout')
}

/**
 * 获取当前登录用户信息
 */
export function getCurrentUser(): Promise<UserInfo> {
  return request.get('/auth/me')
}

/**
 * 更新个人资料
 */
export function  profile(form:UserInfo): Promise<ProfileResponse> {
  return  request.put('/auth/profile',form)
}

/**
 * 修改密码
 */
export function changePassword(data: ChangePasswordRequest): Promise<void> {
  return request.post('/auth/password', data)
}

// ===== 应用信息相关 =====

export interface AppInfo {
  id: number
  appName: string
  appKey?: string
  appSecret?: string
  enabled?: boolean
  description?: string
  creatorId?: number
  createdAt?: string
  updatedAt?: string
}

export interface AppUserInfo {
  id: number
  username: string
  realName?: string
  phone?: string
  email?: string
  photo?: string
  appId?: number
  enabled?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface AppCurrentResponse {
  app: AppInfo
  users: AppUserInfo[]
  isCreator: boolean
}

export interface InviteRequest {
  username: string
  password: string
  realName?: string
  phone?: string
  email?: string
}

/**
 * 获取当前用户所在应用的信息（含应用下所有用户、是否为创建者）
 */
export function getCurrentApp(): Promise<AppCurrentResponse> {
  return request.get('/apps/current')
}

/**
 * 邀请用户加入当前应用（仅应用创建者可操作）
 */
export function inviteUser(data: InviteRequest): Promise<{ id: number; username: string }> {
  return request.post('/apps/invite', data)
}
