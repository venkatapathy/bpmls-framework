import { BpmlsappPage } from './app.po';

describe('bpmlsapp App', () => {
  let page: BpmlsappPage;

  beforeEach(() => {
    page = new BpmlsappPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
