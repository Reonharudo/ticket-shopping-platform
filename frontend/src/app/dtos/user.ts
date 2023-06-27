export interface User {
  id?: number;
  admin: boolean;
  email: string;
  firstName: string;
  lastName: string;
  birthdate: Date;
  address: string;
  areaCode: number;
  cityName: string;
  password: string;
  isLocked: boolean;
}

export interface BlockUser {
  email: string;
  isLocked: boolean;
}

export interface DeleteUser{
  id?: number;
  email: string;
  password: string;
}

export class BlockUser {
  constructor(public email: string,
              public isLocked: boolean) {
  }
}

export interface ResetPasswordUser{
  email: string;
  newPassword: string;
  token: string;
}
