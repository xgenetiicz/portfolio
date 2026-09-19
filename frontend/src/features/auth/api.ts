import client from "../../api/client"; // importing axios
import type { LoginRequest, OtpRequest, LoginResponseDTO } from "./types";

// Step 1: email + password -> backend generates an OTP, emails it, and replies
// with a plain confirmation string ("OTP sent to your email"). 401 means the
// account isn't verified yet.
export async function login(credentials: LoginRequest): Promise<string> {
  const response = await client.post<string>("/auth/login", credentials);
  return response.data;
}

// Step 2: email + the 6-digit code -> backend replies with a JWT + its
// expiry once the code checks out.
export async function verifyOtp(otpRequest: OtpRequest): Promise<LoginResponseDTO> {
  const response = await client.post<LoginResponseDTO>("/auth/verify/otp", otpRequest);
  return response.data;
}