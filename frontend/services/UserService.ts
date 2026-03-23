import axios from "axios";

const USERS_URL = "http://localhost:8080/users";

export class UserService {
    static async createUser(username: string, password: string){
        const response = await axios.post(USERS_URL, {username: username, password: password});
        return response.data;
    }
}