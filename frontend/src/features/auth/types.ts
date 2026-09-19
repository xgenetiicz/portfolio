export interface LoginRequest {
    email: string;
    password: string;
    }

export interface OtpRequest {
    email: string;
    otpCode: string;
    }

export interface LoginResponseDTO {
    token: string;
    expiresIn: number;
    }