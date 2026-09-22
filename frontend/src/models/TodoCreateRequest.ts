export interface TodoCreateRequest {
    readonly task: string,
    readonly due: string | null
}