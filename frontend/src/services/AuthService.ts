import axios from "axios";
import type {TokenRequest} from "../models/TokenRequest.ts";
import type {TokenResponse} from "../models/TokenResponse.ts";

const AUTH_URL = "http://localhost:8080/auth/token";

export class AuthService {

    static async login(request: TokenRequest): Promise<TokenResponse>{
        const response = await axios.post(AUTH_URL, request);
        localStorage.setItem("accessToken", response.data.accessToken);
        return response.data;
    }

    static logout(){
        localStorage.removeItem("accessToken");
    }
}