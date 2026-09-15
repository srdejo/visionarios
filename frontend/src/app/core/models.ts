export type Profile = 'PROFESIONAL' | 'EMPRENDEDOR' | 'EMPRESARIO';
export type Role = 'USER' | 'ADMIN';
export type MaterialType = 'PDF' | 'DOC' | 'XLS' | 'MP3';

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: string | null;
}

export interface UserResponse {
  id: string;
  fullName: string;
  phone: string;
  email: string;
  role: Role;
  profile: Profile;
  profession?: string | null;
  yearsExperience?: number | null;
  currentCompany?: string | null;
  businessProduct?: string | null;
  operatingTime?: string | null;
  companyName?: string | null;
  employeeCount?: number | null;
  yearsWithCompany?: number | null;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}

export interface RegisterRequest {
  fullName: string;
  phone: string;
  email: string;
  password: string;
  profile: Profile;
  profession?: string;
  yearsExperience?: number;
  currentCompany?: string;
  businessProduct?: string;
  operatingTime?: string;
  companyName?: string;
  employeeCount?: number;
  yearsWithCompany?: number;
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
