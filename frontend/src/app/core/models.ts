export type Profile = 'PROFESIONAL' | 'EMPRENDEDOR' | 'EMPRESARIO';
export type Role = 'USER' | 'ADMIN';
export type MaterialType = 'PDF' | 'DOC' | 'XLS' | 'MP3';
export type BusinessCategory =
  | 'SERVICIOS'
  | 'COMIDA_RESTAURANTE'
  | 'RETAIL_COMERCIO'
  | 'TECNOLOGIA'
  | 'SALUD'
  | 'EDUCACION'
  | 'CONSTRUCCION'
  | 'OTRO';

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: string | null;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface UserResponse {
  id: string;
  fullName: string;
  phone: string;
  email: string;
  role: Role;
  profile: Profile;
  birthDate?: string | null;
  profession?: string | null;
  yearsExperience?: number | null;
  currentCompany?: string | null;
  businessProduct?: string | null;
  operatingTime?: string | null;
  companyName?: string | null;
  employeeCount?: number | null;
  yearsWithCompany?: number | null;
  businessCategory?: BusinessCategory | null;
  createdAt: string;
  emailVerified: boolean;
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}

export interface RegisterResponse {
  user: UserResponse;
  message: string;
}

export interface RegisterRequest {
  fullName: string;
  phone: string;
  email: string;
  password: string;
  confirmPassword: string;
  profile: Profile;
  birthDate?: string;
  profession?: string;
  yearsExperience?: number;
  currentCompany?: string;
  businessProduct?: string;
  operatingTime?: string;
  companyName?: string;
  employeeCount?: number;
  yearsWithCompany?: number;
  businessCategory?: BusinessCategory;
}

export interface UpdateProfileRequest {
  fullName: string;
  phone: string;
  birthDate?: string;
  profession?: string;
  yearsExperience?: number;
  currentCompany?: string;
  businessProduct?: string;
  operatingTime?: string;
  companyName?: string;
  employeeCount?: number;
  yearsWithCompany?: number;
  businessCategory?: BusinessCategory;
}

export interface EventResponse {
  id: string;
  title: string;
  startsAt: string;
  place: string;
  description: string;
  targetProfile: Profile | null;
  confirmedCount: number;
  confirmedByMe: boolean;
}

export interface EventRequest {
  title: string;
  startsAt: string;
  place: string;
  description: string;
  targetProfile?: Profile | null;
}

export interface MaterialResponse {
  id: string;
  title: string;
  type: MaterialType;
  meta: string;
  driveUrl: string;
  visibleProfile: Profile | null;
  createdAt: string;
}

export interface MaterialRequest {
  title: string;
  type: MaterialType;
  meta: string;
  driveUrl: string;
  visibleProfile?: Profile | null;
}

export interface AdminUsersResponse {
  users: UserResponse[];
  total: number;
  countByProfile: Record<Profile, number>;
  page: number;
  size: number;
  totalFiltered: number;
  totalPages: number;
}

export interface AdminUsersQuery {
  search?: string;
  profile?: Profile;
  businessCategory?: BusinessCategory;
  minAge?: number;
  maxAge?: number;
  minEmployees?: number;
  maxEmployees?: number;
  page?: number;
  size?: number;
}

export interface AdminInviteResponse {
  id: string;
  token: string;
  email: string;
  expiresAt: string;
  used: boolean;
  expired: boolean;
}

export const PROFILE_LABELS: Record<Profile, string> = {
  PROFESIONAL: 'Profesional',
  EMPRENDEDOR: 'Emprendedor',
  EMPRESARIO: 'Empresario',
};

export const BUSINESS_CATEGORY_LABELS: Record<BusinessCategory, string> = {
  SERVICIOS: 'Servicios',
  COMIDA_RESTAURANTE: 'Comida/Restaurante',
  RETAIL_COMERCIO: 'Retail/Comercio',
  TECNOLOGIA: 'Tecnología',
  SALUD: 'Salud',
  EDUCACION: 'Educación',
  CONSTRUCCION: 'Construcción',
  OTRO: 'Otro',
};

export function initialsOf(fullName: string | undefined | null): string {
  return (fullName ?? '')
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((n) => n[0]?.toUpperCase())
    .join('');
}

export function ageFrom(birthDate: string | undefined | null): number | null {
  if (!birthDate) return null;
  const birth = new Date(birthDate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const hasHadBirthdayThisYear =
    today.getMonth() > birth.getMonth() ||
    (today.getMonth() === birth.getMonth() && today.getDate() >= birth.getDate());
  if (!hasHadBirthdayThisYear) age--;
  return age;
}
