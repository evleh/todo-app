export interface TodoResponse{
    readonly id: string,
    readonly task: string,
    readonly due: Date,
    readonly done: boolean,
    readonly ownerId: string,
}