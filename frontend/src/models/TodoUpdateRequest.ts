export interface TodoUpdateRequest {
    readonly id: string,
    readonly task: string,
    readonly due: string | null,
    readonly done: boolean,
}