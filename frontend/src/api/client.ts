import axios from "axios";
import { VITE_API_BASE_URL } from "../config";

const client = axios.create({
  baseURL: `${VITE_API_BASE_URL}/api`,
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem("buildhub_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default client;