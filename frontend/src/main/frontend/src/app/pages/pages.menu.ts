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
                title: 'Demo Video',
              },
            },
          },
          
          {
            path: 'availablelearningpaths',
            data: {
              menu: {
                title: 'Available Learning Paths',
              },
            },
          },
           {
            path: 'runninglearningpaths',
            data: {
              menu: {
                title: 'Pending Learning Paths',
              },
            },
          },
           {
            path: 'surveyhome',
            data: {
              menu: {
                title: 'Survey Form',
              },
            },
          },
        ],
      },
    ],
  },
];
