export interface TodoResponse{
    readonly id: string,
    readonly task: string,
    readonly due: string | null,
    readonly done: boolean,
    readonly ownerId: string,
    readonly parentId: string | null,
    readonly subtasks: Array<TodoResponse>,
}