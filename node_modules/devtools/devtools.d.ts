/// <reference types="node"/>
/// <reference types="webdriver"/>

declare namespace WebDriver {
    interface ChromeOptions {
        /**
         * `devtools` only, switch headless mode by either `headless` flag or `--headless` argument but not both
         */
        headless?: boolean;
    }

    interface FirefoxOptions {
        /**
         * `devtools` only, switch headless mode by either `headless` flag or `--headless` argument but not both
         */
        headless?: boolean;
    }

    interface MicrosoftEdgeOptions {
        /**
         * `devtools` only, switch headless mode by either `headless` flag or `--headless` argument but not both
         */
        headless?: boolean;
    }
}

declare namespace DevTools {
    function newSession(
        options?: WebDriver.Options,
        modifier?: (...args: any[]) => any,
        proto?: object,
        commandWrapper?: (commandName: string, fn: (...args: any[]) => any) => any
    ): Promise<Client>;

    function reloadSession(
        instance: Client
    ): Promise<Client>;

    // generated typings
        // devtools types
    interface Client {
        newSession(capabilities: object): object;
        deleteSession(): void;
        status(): object;
        getTimeouts(): object;
        setTimeouts(implicit?: number, pageLoad?: number, script?: number): void;
        getUrl(): string;
        navigateTo(url: string): string;
        back(): void;
        forward(): void;
        refresh(): void;
        getTitle(): string;
        getWindowHandle(): string;
        closeWindow(): void;
        switchToWindow(handle: string): void;
        createWindow(type: 'tab' | 'window'): object;
        getWindowHandles(): string[];
        printPage(orientation?: string, scale?: number, background?: boolean, width?: number, height?: number, top?: number, bottom?: number, left?: number, right?: number, shrinkToFit?: boolean, pageRanges?: object[]): string;
        switchToFrame(id: (number|object|null)): void;
        switchToParentFrame(): void;
        getWindowRect(): object;
        setWindowRect(x: (number|null), y: (number|null), width: (number|null), height: (number|null)): object;
        maximizeWindow(): object;
        minimizeWindow(): object;
        fullscreenWindow(): object;
        findElement(using: string, value: string): WebDriver.ElementReference[];
        findElements(using: string, value: string): WebDriver.ElementReference[];
        findElementFromElement(using: string, value: string): WebDriver.ElementReference;
        findElementsFromElement(using: string, value: string): WebDriver.ElementReference[];
        getActiveElement(): string;
        isElementSelected(): boolean;
        isElementDisplayed(): boolean;
        getElementAttribute(name: string): string;
        getElementProperty(name: string): string;
        getElementCSSValue(propertyName: string): string;
        getElementText(): string;
        getElementTagName(): string;
        getElementRect(): object;
        isElementEnabled(): boolean;
        elementClick(): void;
        elementClear(): void;
        elementSendKeys(text: string): void;
        getPageSource(): string;
        executeScript(script: string, args?: (string|object|number|boolean|undefined)[]): any;
        executeAsyncScript(script: string, args: (string|object|number|boolean|undefined)[]): any;
        getAllCookies(): object[];
        addCookie(cookie: object): void;
        deleteAllCookies(): void;
        getNamedCookie(name: string): object;
        deleteCookie(name: string): void;
        performActions(actions: object[]): void;
        releaseActions(): void;
        dismissAlert(): void;
        acceptAlert(): void;
        getAlertText(): string;
        sendAlertText(text: string): void;
        takeScreenshot(): string;
        takeElementScreenshot(scroll?: boolean): string;
        getElementComputedRole(): string;
        getElementComputedLabel(): string;
    }

}

declare module "devtools" {
    export default DevTools;

    const SUPPORTED_BROWSER: string[]
    export { SUPPORTED_BROWSER }
}
