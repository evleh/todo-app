import type {TodoResponse} from "../models/TodoResponse.ts";
import type {TodoCreateRequest} from "../models/TodoCreateRequest.ts";
import type {TodoUpdateRequest} from "../models/TodoUpdateRequest.ts";
import axios from "axios";

const URL_TODOS = "http://localhost:8080/todos";

export class TodoService{
    static async readAll(): Promise<Array<TodoResponse>>{
        const response = await axios.get(URL_TODOS, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });
        return response.data;
    }

    static async create(request: TodoCreateRequest){
        const response = await axios.post(URL_TODOS , request, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });
        return response.data;
    }

    static async update(request: TodoUpdateRequest){
        const response = await axios.put(`${URL_TODOS}/${request.id}`, request, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });
        return response.data;
    }

    static async delete(id: string){
        const response = await axios.delete(`${URL_TODOS}/${id}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });
        return response.data;
    }
}