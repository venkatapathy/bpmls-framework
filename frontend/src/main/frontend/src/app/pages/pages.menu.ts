export const PAGES_MENU = [
  {
    
    path: 'pages',
    children: [
        
      {
        path: 'home',
        data: {
          menu: {
            title: 'general.menu.home',
            icon: 'ion-gear-a',
            selected: true,
            expanded: true,
            order: 250,
          },
        },
        children: [
          {
            path: 'demohome',
            data: {
              menu: {
                title: '1. Demo Video',
              },
            },
          },
          
          {
            path: 'availablelearningpaths',
            data: {
              menu: {
                title: '2.1 Available Learning Paths',
              },
            },
          },
           {
            path: 'runninglearningpaths',
            data: {
              menu: {
                title: '2.2 Running Learning Paths',
              },
            },
          },
           {
            path: 'surveyhome',
            data: {
              menu: {
                title: '3. Survey Home',
              },
            },
          },
        ],
      },
    ],
  },
];
