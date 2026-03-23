import axios from "axios";
import type {UserResponse} from "../models/UserResponse.ts";
import type {UserCreateRequest} from "../models/UserCreateRequest.ts";

const USERS_URL = "http://localhost:8080/users";

export class UserService {
    static async createUser(request: UserCreateRequest): Promise<UserResponse>{
        const response = await axios.post(USERS_URL, request);
        return response.data;
    }
}