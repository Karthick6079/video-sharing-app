import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class UiStateService {
    private showAnimationSubject = new BehaviorSubject<boolean>(true);
    showAnimation$ = this.showAnimationSubject.asObservable();

    showAnimation() {
        this.showAnimationSubject.next(true);
    }

    hideAnimation() {
        this.showAnimationSubject.next(false);
    }
}
