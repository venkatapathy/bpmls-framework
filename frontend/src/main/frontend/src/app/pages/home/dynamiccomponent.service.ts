import { ViewContainerRef, Type, Component, Injectable, AfterViewInit } from '@angular/core';
import { WindowRefService } from './window.ref.service';
import { AdHocComponentFactoryCreator } from './adhoc-component-factory.service';
import { LearningSimulator } from './learningSimulator/learningSimulator.component'
declare var introJs: any;

@Injectable()
export class DynamicComponentService {
    constructor(private adHocComponentFactoryCreator: AdHocComponentFactoryCreator) {

    }
    private getFlowDiagramComponent(flowdata: string) {

        @Component({
            template: '<div id="diagram" style="height: 100px;"></div>'
        })
        class InsertedComponent implements AfterViewInit {


            _window: any;

            constructor(windowRef: WindowRefService) {
                this._window = windowRef.nativeWindow;


            }

            ngAfterViewInit() {
                // this.viewer = new BpmnViewer({ container: '#canvas' });
                var flowChart = (<any>this._window).flowchart;
                // console.log(flowdata);
                var diagram = flowChart.parse(flowdata);// the other symbols too...
                diagram.drawSVG('diagram', {
                    // 'x': 30,
                    // 'y': 50,
                    'line-width': 3,
                    'maxWidth': 3,//ensures the flowcharts fits within a certian width
                    'line-length': 80,
                    'text-margin': 10,
                    'font-size': 14,
                    'font': 'normal',
                    'font-family': 'Helvetica',
                    'font-weight': 'normal',
                    'font-color': 'black',
                    'line-color': 'black',
                    'element-color': 'black',
                    'fill': 'white',
                    'yes-text': 'yes',
                    'no-text': 'no',
                    'arrow-end': 'block',
                    'scale': 1,
                    'symbols': {
                        'start': {
                            'font-color': 'red',
                            'element-color': 'green',
                            'fill': 'white'
                        },
                        'end': {
                            'class': 'end-element'
                        }
                    },
                    'flowstate': {
                        'past': { 'fill': 'green', 'font-size': 12 },
                        'current': { 'fill': 'yellow', 'font-color': 'red', 'font-weight': 'bold' },
                        'future': { 'fill': 'white' },
                        'request': { 'fill': 'blue' },
                        'invalid': { 'fill': '#444444' },
                        'approved': { 'fill': '#58C4A3', 'font-size': 12, 'yes-text': 'APPROVED', 'no-text': 'n/a' },
                        'rejected': { 'fill': '#C45879', 'font-size': 12, 'yes-text': 'n/a', 'no-text': 'REJECTED' }
                    }
                });
            }



        }

        return InsertedComponent;
    }

    public createFlowDiagramComponent(viewContainer: ViewContainerRef, flowdata: string) {
        const component = this.getFlowDiagramComponent(flowdata);
        const componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
        viewContainer.clear();
        const componentRef = viewContainer.createComponent(componentFactory);
        componentRef.changeDetectorRef.detectChanges();

    }

    private getDynamicFormComponentwithModel(taskform: string, modelJs: JSON, prompt: JSON,
        simulatorComponent: LearningSimulator): Type<any> {
        @Component({
            template: taskform
        })
        class InsertedComponent {
            learningForm = modelJs;
            _window: any;
            constructor() {

                //alert(JSON.stringify(this.learningForm));
            }
            private completeLearning(f) {

                //alert(JSON.stringify(f.value));
                simulatorComponent.completeLearning(JSON.stringify(f.value));

            }
            startIntro() {
                var intro = introJs();
                intro.setOptions(prompt);

                intro.start();
            }

            startLs(lpinstid) {
                simulatorComponent.startLearningScenario(lpinstid);
                //alert("hi");
            }
        }

        return InsertedComponent;


    }

    public createDynamicFormComponentwithModel(viewContainer: ViewContainerRef, taskform: string,
        modelJs: JSON, prompt: JSON, simulatorComponent: LearningSimulator) {
        const component = this.getDynamicFormComponentwithModel(taskform, modelJs, prompt, simulatorComponent);
        const componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
        viewContainer.clear();
        const componentRef = viewContainer.createComponent(componentFactory);

        componentRef.changeDetectorRef.detectChanges();
    }

    private getProcessDiagramComponent(xmlData: string, available: JSON[],
        running: JSON, completed: JSON[], trace: JSON[]) {
        @Component({
            template: '<div id="canvas" style="height: 600px;background-color:white;"></div>'
        })
        class InsertedComponent implements AfterViewInit {

            _window: any;
            constructor(windowRef: WindowRefService) {
                this._window = windowRef.nativeWindow;

            }
            ngAfterViewInit() {
                // this.viewer = new BpmnViewer({ container: '#canvas' });
                this.loadSampleBPMN(available, running, completed, trace);
            }
            loadSampleBPMN(available: JSON[],
                running: any, completed: JSON[], trace: JSON[]) {
                const bpmnNavigatedViewer = (<any>this._window).BpmnJS;
                const viewer = new bpmnNavigatedViewer({ container: '#canvas' });
                viewer.importXML(xmlData, function (err) {

                    if (err) {
                        console.log(err);
                        return;
                    }




                    // available coloring yellow
                    for (var i = 0; i < available.length; i++) {
                        var obj: any = available[i];

                        // console.log(obj.taskid);
                        const overlays = viewer.get('overlays');
                        const elementRegistry = viewer.get('elementRegistry');
                        const taskName = obj.taskid;
                        const shape = elementRegistry.get(taskName);

                        const $overlayHtml = $('<div class="highlight-available-overlay">')
                            .css({
                                width: shape.width,
                                height: shape.height
                            });

                        overlays.add(taskName, {
                            position: {
                                top: 0,
                                left: 0
                            },
                            html: $overlayHtml
                        });
                    }

                    // completed coloring green
                    for (var i = 0; i < completed.length; i++) {
                        var obj: any = completed[i];

                        //console.log("hello "+obj.taskid);
                        const overlays = viewer.get('overlays');
                        const elementRegistry = viewer.get('elementRegistry');
                        const taskName = obj.taskid;
                        const shape = elementRegistry.get(taskName);

                        const $overlayHtml = $('<div class="highlight-completed-overlay">')
                            .css({
                                width: shape.width,
                                height: shape.height
                            });

                        overlays.add(taskName, {
                            position: {
                                top: 0,
                                left: 0
                            },
                            html: $overlayHtml
                        });
                    }

                    // completed trace grey
                    for (var i = 0; i < trace.length; i++) {
                        var obj: any = trace[i];

                        console.log(obj.taskid);
                        const overlays = viewer.get('overlays');
                        const elementRegistry = viewer.get('elementRegistry');
                        const taskName = obj.taskid;
                        const shape = elementRegistry.get(taskName);

                        const $overlayHtml = $('<div class="highlight-trace-overlay">')
                            .css({
                                width: shape.width,
                                height: shape.height
                            });

                        overlays.add(taskName, {
                            position: {
                                top: 0,
                                left: 0
                            },
                            html: $overlayHtml
                        });
                    }

                    //running
                    const overlays = viewer.get('overlays');
                    const elementRegistry = viewer.get('elementRegistry');
                    const taskName = running.taskid;
                    const shape = elementRegistry.get(taskName);

                    const $overlayHtml = $('<div class="highlight-running-overlay">')
                        .css({
                            width: shape.width,
                            height: shape.height
                        });

                    overlays.add(taskName, {
                        position: {
                            top: 0,
                            left: 0
                        },
                        html: $overlayHtml
                    });

                    /*const overlays = viewer.get('overlays');
                    const elementRegistry = viewer.get('elementRegistry');

                    const shape = elementRegistry.get('_6-695');

                    const $overlayHtml = $('<div class="highlight-overlay">')
                        .css({
                            width: shape.width,
                            height: shape.height
                        });

                    overlays.add('_6-695', {
                        position: {
                            top: 0,
                            left: 0
                        },
                        html: $overlayHtml
                    });*/
                });
            }

            handleError(err: any) {

                console.log(err);
                if (err) {
                    console.log('error rendering', err);
                } else {
                    console.log('rendered');

                }


            }
        }

        return InsertedComponent;
    }

    public createProcessDiagramComponent(viewContainer: ViewContainerRef, xmlData: string,
        available: JSON[], running: JSON, completed: JSON[], trace: JSON[]) {
        const component = this.getProcessDiagramComponent(xmlData, available, running, completed, trace);
        const componentFactory = this.adHocComponentFactoryCreator.getFactory(component);
        viewContainer.clear();
        const componentRef = viewContainer.createComponent(componentFactory);
        componentRef.changeDetectorRef.detectChanges();
    }
}
