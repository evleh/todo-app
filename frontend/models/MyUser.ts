export class MyUser {
    readonly id: string;
    readonly username: string;
    readonly role: string;

    constructor(id : string,  username: string, role: string) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

}