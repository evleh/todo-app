export interface TodoResponse{
    readonly id: string,
    readonly task: string,
    readonly due: Date,
    readonly done: boolean,
    readonly ownerId: string,
    readonly parentId: string | null,
    readonly subtasks: Array<TodoResponse>,
}